package com.cinefy.backend.controller;

import com.cinefy.backend.dto.HistoryResponse;
import com.cinefy.backend.model.Movie;
import com.cinefy.backend.model.WatchSession;
import com.cinefy.backend.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public List<HistoryResponse> getHistory(Authentication authentication) {
        return historyService.getHistory(authentication.getName());
    }
}
