package com.cinefy.backend.controller;

import com.cinefy.backend.model.MovieGenre;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.cinefy.backend.repository.MovieRepository;
import com.cinefy.backend.model.Movie;
import com.cinefy.backend.dto.AdminMovieRequest;
import com.cinefy.backend.repository.MovieGenreRepository;
import com.cinefy.backend.repository.MovieDailyStatsRepository;
import com.cinefy.backend.repository.GenreRepository;
import com.cinefy.backend.dto.MovieResponse;
import com.cinefy.backend.model.Genre;
import com.cinefy.backend.model.MovieGenreId;
import com.cinefy.backend.model.MovieDailyStats;
import com.cinefy.backend.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MovieRepository movieRepository;
    private final MovieGenreRepository movieGenreRepository;
    private final GenreRepository genreRepository;
    private final FileStorageService fileStorageService;

    @PostMapping(value = "/movies", consumes = "multipart/form-data")
    public Movie addMovie(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String language,
            @RequestParam Integer releaseYear,
            @RequestParam Integer durationMin,
            @RequestParam("poster") MultipartFile poster,
            @RequestParam List<Short> genreIds
    ) {

        // 1️⃣ Store image
        String fileName = fileStorageService.storeFile(poster);

        // 2️⃣ Create movie
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setLanguage(language);
        movie.setReleaseYear(releaseYear);
        movie.setDurationMin(durationMin);

        movie.setPosterUrl("/uploads/" + fileName);

        movie.setAvgRating(0.0);
        movie.setRatingsCount(0L);
        movie.setTotalViews(0L);
        movie.setIsActive(true);

        movieRepository.save(movie);

        // 3️⃣ Save genres
        for (Short genreId : genreIds) {

            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new RuntimeException("Genre not found"));

            MovieGenreId mid = new MovieGenreId(movie.getId(), genreId);

            MovieGenre movieGenre = new MovieGenre();
            movieGenre.setId(mid);
            movieGenre.setMovie(movie);
            movieGenre.setGenre(genre);

            movieGenreRepository.save(movieGenre);
        }

        return movie;
    }

    @GetMapping("/movies")
    public List<Movie> getAllMoviesAdmin() {
        return movieRepository.findAll();
    }
    @DeleteMapping("/movies/{id}")
    public void deleteMovie(@PathVariable UUID id) {

        movieRepository.deleteById(id);
    }


}