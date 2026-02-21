package com.cinefy.backend.repository;

import com.cinefy.backend.model.MovieReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovieReviewRepository extends JpaRepository<MovieReview, UUID> {
    List<MovieReview> findByMovieIdOrderByCreatedAtDesc(UUID movieId);
}
