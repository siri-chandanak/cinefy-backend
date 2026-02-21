package com.cinefy.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_movie_ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserMovieRatingId.class)
public class UserMovieRating {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "movie_id")
    private UUID movieId;

    private int rating;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
