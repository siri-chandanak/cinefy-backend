package com.cinefy.backend.service;

import com.cinefy.backend.dto.MovieResponse;
import com.cinefy.backend.model.Movie;
import com.cinefy.backend.repository.MovieRepository;
import com.cinefy.backend.repository.UserRepository;
import com.cinefy.backend.repository.RecommendationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationQueryRepository queryRepo;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public List<Movie> getRecommendations(String email, int limit) {

        UUID userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email))
                .getId();

        // 1) Try content-based: top genres from watched + liked
        List<Long> topGenreIds = queryRepo.findTopGenreIdsForUser(userId, 5);

        if (!topGenreIds.isEmpty()) {
            List<UUID> movieIds = queryRepo.findMoviesByGenresExcludingWatched(userId, topGenreIds, limit);
            List<Movie> movies = movieRepository.findAllById(movieIds);
            if (!movies.isEmpty()) return preserveOrder(movies, movieIds);
        }

        // 2) Collaborative: similar users -> what they watched
        List<UUID> similarUsers = queryRepo.findSimilarUsers(userId, 5);
        if (!similarUsers.isEmpty()) {
            List<UUID> movieIds = queryRepo.findMoviesWatchedByUsersExcludingWatched(userId, similarUsers, limit);
            List<Movie> movies = movieRepository.findAllById(movieIds);
            if (!movies.isEmpty()) return preserveOrder(movies, movieIds);
        }

        // 3) Fallback: trending
        List<UUID> trendingIds = queryRepo.findTrendingMovieIds(limit);
        List<Movie> trending = movieRepository.findAllById(trendingIds);
        return preserveOrder(trending, trendingIds);
    }

    private List<Movie> preserveOrder(List<Movie> movies, List<UUID> idsInOrder) {
        Map<UUID, Movie> map = new HashMap<>();
        for (Movie m : movies) map.put(m.getId(), m);

        List<Movie> ordered = new ArrayList<>();
        for (UUID id : idsInOrder) {
            Movie m = map.get(id);
            if (m != null) ordered.add(m);
        }
        return ordered;
    }


}
