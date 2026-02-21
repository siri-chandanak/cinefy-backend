package com.cinefy.backend.controller;

import com.cinefy.backend.model.Genre;
import com.cinefy.backend.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreRepository genreRepository;

    @GetMapping
    public List<Genre> getGenres() {
        return genreRepository.findAll();
    }
}
