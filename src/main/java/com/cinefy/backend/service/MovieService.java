package com.cinefy.backend.service;

import com.cinefy.backend.model.Movie;
import com.cinefy.backend.model.User;
import com.cinefy.backend.model.MovieLike;
import com.cinefy.backend.model.MovieReview;
import com.cinefy.backend.model.UserMovieRating;
import com.cinefy.backend.model.WatchSession;
import com.cinefy.backend.repository.MovieRepository;
import com.cinefy.backend.repository.UserRepository;
import com.cinefy.backend.repository.WatchSessionRepository;
import com.cinefy.backend.repository.MovieLikeRepository;
import com.cinefy.backend.repository.MovieReviewRepository;
import com.cinefy.backend.repository.UserMovieRatingRepository;
import com.cinefy.backend.repository.MovieDailyStatsRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final WatchSessionRepository watchSessionRepository;
    private final MovieLikeRepository movieLikeRepository;
    private final UserMovieRatingRepository ratingRepository;
    private final MovieReviewRepository reviewRepository;
    private final AnalyticsService analyticsService;
    private final MovieDailyStatsRepository movieDailyStatsRepository;

    public int getUserRating(String email, UUID movieId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ratingRepository
                .findByUserIdAndMovieId(user.getId(), movieId)
                .map(UserMovieRating::getRating)
                .orElse(0);
    }

    @Transactional
    public void rateMovie(String email, UUID movieId, int ratingValue) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        UserMovieRating rating = ratingRepository
                .findByUserIdAndMovieId(user.getId(), movieId)
                .orElse(null);

        if (rating == null) {
            rating = new UserMovieRating();
            rating.setUserId(user.getId());
            rating.setMovieId(movieId);
        }

        rating.setRating(ratingValue);
        rating.setUpdatedAt(Instant.now());

        ratingRepository.save(rating);

        // OPTIONAL: update avg_rating & ratings_count
        updateMovieRatingStats(movieId);
        analyticsService.incrementDailyStats(
                movieId,
                0,      // views_count increment
                0,      // watch_count increment
                0,
                0,
                1,
                0
        );
    }

    private void updateMovieRatingStats(UUID movieId) {

        List<UserMovieRating> ratings =
                ratingRepository.findAll().stream()
                        .filter(r -> r.getMovieId().equals(movieId))
                        .toList();

        if (ratings.isEmpty()) return;

        double avg = ratings.stream()
                .mapToInt(UserMovieRating::getRating)
                .average()
                .orElse(0);

        Movie movie = movieRepository.findById(movieId).orElseThrow();

        movie.setAvgRating(avg);
        movie.setRatingsCount((long) ratings.size());

        movieRepository.save(movie);
    }

    @Transactional
    public void addReview(String email, UUID movieId, String comment) {

        UUID userId = userRepository.findByEmail(email)
                .orElseThrow()
                .getId();

        MovieReview review = MovieReview.builder()
                .userId(userId)
                .movieId(movieId)
                .comment(comment)
                .createdAt(java.time.Instant.now())
                .build();

        reviewRepository.save(review);
        analyticsService.incrementDailyStats(
                movieId,
                0,      // views_count increment
                0,      // watch_count increment
                0,
                0,
                0,
                1
        );

    }
    public List<MovieReview> getReviews(UUID movieId) {
        return reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId);
    }



    public boolean isWatched(String email, UUID movieId)
    {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return watchSessionRepository.existsByUserAndMovie_Id(user, movieId);
    }

    @Transactional
    public void watchMovie(String email, UUID movieId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        // Prevent duplicates
        if (watchSessionRepository.existsByUserAndMovie_Id(user, movieId)) {
            return;
        }

        WatchSession session = new WatchSession();
        session.setUser(user);
        session.setMovie(movie);
        session.setStartedAt(Instant.now());

        watchSessionRepository.save(session);
        long movieMinutes = movie.getDurationMin();

        analyticsService.incrementDailyStats(
                movieId,
                1,      // views_count increment
                1,      // watch_count increment
                movieMinutes,
                0,
                0,
                0
        );
    }

    @Transactional
    public void removeWatch(String email, UUID movieId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        watchSessionRepository.deleteByUserAndMovie_Id(user, movieId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        long movieMinutes = 0 - movie.getDurationMin();
        analyticsService.incrementDailyStats(
                movieId,
                0,      // views_count increment
                -1,      // watch_count increment
                movieMinutes,
                0,
                0,
                0
        );
    }

    public boolean isMovieLiked(String email, UUID movieId) {
        return movieLikeRepository.existsByUserEmailAndMovieId(email, movieId);
    }

    @Transactional
    public void likeMovie(String email, UUID movieId) {
        if (movieLikeRepository.existsByUserEmailAndMovieId(email, movieId)) return;

        movieLikeRepository.save(MovieLike.builder()
                .id(UUID.randomUUID())
                .userEmail(email)
                .movieId(movieId)
                .createdAt(Instant.now())
                .build());
        analyticsService.incrementDailyStats(
                movieId,
                0,      // views_count increment
                0,      // watch_count increment
                0,
                1,
                0,
                0
        );
    }

    @Transactional
    public void unlikeMovie(String email, UUID movieId) {
        movieLikeRepository.deleteByUserEmailAndMovieId(email, movieId);
        analyticsService.incrementDailyStats(
                movieId,
                0,      // views_count increment
                0,      // watch_count increment
                0,
                -1,
                0,
                0
        );
    }

    public List<Movie> getTrendingMovies(int limit) {

        List<Object[]> rows = movieDailyStatsRepository.getTodayTrending(limit);

        List<UUID> ids = rows.stream()
                .map(r -> (UUID) r[0])
                .toList();

        if (ids.isEmpty()) {
            return movieRepository.findAll().stream().limit(limit).toList();
        }

        List<Movie> movies = movieRepository.findAllById(ids);

        // Preserve order of trending score
        Map<UUID, Movie> movieMap =
                movies.stream().collect(Collectors.toMap(Movie::getId, m -> m));

        return ids.stream()
                .map(movieMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

}
