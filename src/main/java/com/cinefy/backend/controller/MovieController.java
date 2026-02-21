package com.cinefy.backend.controller;

import com.cinefy.backend.model.Movie;
import com.cinefy.backend.dto.RatingRequest;
import com.cinefy.backend.model.MovieReview;
import com.cinefy.backend.repository.MovieRepository;
import com.cinefy.backend.dto.ReviewRequest;
import com.cinefy.backend.repository.MovieReviewRepository;
import org.springframework.security.core.Authentication;
import com.cinefy.backend.service.MovieService;
import com.cinefy.backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final MovieReviewRepository reviewRepository;
    private final AnalyticsService analyticsService;

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable UUID id)
    {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        // increment daily view
        analyticsService.incrementDailyStats(id,1,0,0,0,0,0);

        return movie;
    }
    @GetMapping("/trending")
    public List<Movie> getTrendingMovies() {
        return movieService.getTrendingMovies(10);
    }

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }




    @GetMapping("/{id}/watched")
    public boolean isWatched(@PathVariable UUID id, Authentication auth){
        String email = auth.getName();
        return movieService.isWatched(email,id);
    }
    @PostMapping("/{id}/watch")
    public void watchMovie(@PathVariable UUID id, Authentication auth) {
        String email = auth.getName();
        movieService.watchMovie(email, id);
    }
    @DeleteMapping("/{id}/watch")
    public void removeWatch(@PathVariable UUID id, Authentication auth) {
        String email = auth.getName();
        movieService.removeWatch(email, id);
    }


    @GetMapping("/{id}/liked")
    public boolean isLiked(@PathVariable UUID id, Authentication auth) {
        String email = auth.getName();
        return movieService.isMovieLiked(email, id);
    }
    @PostMapping("/{id}/like")
    public void likeMovie(@PathVariable UUID id, Authentication auth) {
        String email = auth.getName();
        movieService.likeMovie(email, id);
    }
    @DeleteMapping("/{id}/like")
    public void unlikeMovie(@PathVariable UUID id, Authentication auth) {
        String email = auth.getName();
        movieService.unlikeMovie(email, id);
    }



    @PostMapping("/{id}/rate")
    public void rateMovie(@PathVariable UUID id,
                          @RequestBody RatingRequest request,
                          Authentication auth) {

        movieService.rateMovie(auth.getName(), id, request.getRating());
    }

    @GetMapping("/{id}/rating")
    public int getUserRating(@PathVariable UUID id, Authentication auth) {
        return movieService.getUserRating(auth.getName(), id);
    }


    @GetMapping("/{id}/reviews")
    public List<MovieReview> getReviews(@PathVariable UUID id) {
        return reviewRepository.findByMovieIdOrderByCreatedAtDesc(id);
    }
    @PostMapping("/{id}/review")
    public void addReview(@PathVariable UUID id,
                          @RequestBody ReviewRequest req,
                          Authentication auth) {
        movieService.addReview(auth.getName(), id, req.getComment());
    }
}
