package com.cinefy.backend.repository;

import com.cinefy.backend.model.UserMovieRating;
import com.cinefy.backend.model.UserMovieRatingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserMovieRatingRepository extends JpaRepository<UserMovieRating, UserMovieRatingId> {
    Optional<UserMovieRating> findByUserIdAndMovieId(UUID userId, UUID movieId);
}
