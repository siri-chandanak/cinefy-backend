package com.cinefy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MovieResponse {
    private UUID id;
    private String title;
    private String posterUrl;
    private String language;
    private Integer releaseYear;
    private Double avgRating;
}
