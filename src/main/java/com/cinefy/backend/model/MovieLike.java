package com.cinefy.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "movie_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_email", "movie_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class MovieLike {

    @Id
    private UUID id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "movie_id", nullable = false)
    private UUID movieId;

    @Column(name = "created_at")
    private Instant createdAt;
}
