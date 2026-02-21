package com.cinefy.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name="movie_daily_stats")
@Getter
@Setter
@IdClass(MovieDailyStatsId.class)
@NoArgsConstructor
public class MovieDailyStats {

    @Id
    @Column(name = "movie_id", nullable = false)
    private UUID movieId;

    @Column(name="day", nullable=false)
    private LocalDate day;

    @Column(name="views_count", nullable=false)
    private int viewsCount = 0;

    @Column(name="watch_count", nullable=false)
    private int watchCount = 0;

    @Column(name="likes_count", nullable=false)
    private int likesCount = 0;

    @Column(name="ratings_count", nullable=false)
    private int ratingsCount = 0;

    @Column(name="reviews_count", nullable=false)
    private int reviewsCount = 0;

    @Column(name="updated_at", nullable=false)
    private Instant updatedAt = Instant.now();
}
