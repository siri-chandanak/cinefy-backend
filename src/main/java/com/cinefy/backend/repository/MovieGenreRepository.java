package com.cinefy.backend.repository;

import com.cinefy.backend.model.MovieGenre;
import com.cinefy.backend.model.MovieGenreId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieGenreRepository extends JpaRepository<MovieGenre, MovieGenreId> {

    // Get all genres for a movie
    List<MovieGenre> findByIdMovieId(UUID movieId);

    // Delete all genres for a movie (useful for edit)
    void deleteByIdMovieId(UUID movieId);

    // Get all movies for a genre
    List<MovieGenre> findByIdGenreId(Short genreId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MovieGenre mg WHERE mg.movie.id = :movieId")
    void deleteByMovieId(UUID movieId);

}
