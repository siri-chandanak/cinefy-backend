package com.cinefy.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    private String description;

    private String language;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name="duration_min")
    private Integer durationMin;

    @Column(name="poster_url")
    private String posterUrl;

    @Column(name="avg_rating")
    private Double avgRating;

    @Column(name = "ratings_count")
    private Long ratingsCount;

    @Column(name = "total_views")
    private Long totalViews;

    @Column(name = "is_active")
    private Boolean isActive;

}
