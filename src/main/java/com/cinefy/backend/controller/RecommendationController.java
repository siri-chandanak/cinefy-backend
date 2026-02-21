package com.cinefy.backend.controller;

import com.cinefy.backend.service.RecommendationService;
import com.cinefy.backend.model.Movie;
import com.cinefy.backend.dto.MovieResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public List<Movie> getRecommendations(Authentication auth,
                                         @RequestParam(defaultValue = "20") int limit)
    {
        String email = auth.getName();
        return recommendationService.getRecommendations(email, limit);
    }

}
