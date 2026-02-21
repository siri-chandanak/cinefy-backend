package com.cinefy.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminMovieRequest {
    private String title;
    private String description;
    private String language;
    private Integer releaseYear;
    private Integer durationMin;
    private String posterUrl;
    private List<Short> genreIds;
}
