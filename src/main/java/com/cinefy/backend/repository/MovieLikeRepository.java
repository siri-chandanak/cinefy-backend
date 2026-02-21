package com.cinefy.backend.repository;

import com.cinefy.backend.model.MovieLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MovieLikeRepository extends JpaRepository<MovieLike, UUID> {
    boolean existsByUserEmailAndMovieId(String userEmail, UUID movieId);
    void deleteByUserEmailAndMovieId(String userEmail, UUID movieId);
}