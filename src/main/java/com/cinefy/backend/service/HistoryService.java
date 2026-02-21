package com.cinefy.backend.service;

import com.cinefy.backend.dto.HistoryResponse;
import com.cinefy.backend.model.Movie;
import com.cinefy.backend.model.User;
import com.cinefy.backend.model.WatchSession;
import com.cinefy.backend.repository.MovieRepository;
import com.cinefy.backend.repository.UserRepository;
import com.cinefy.backend.repository.WatchSessionRepository;
import com.cinefy.backend.repository.MovieDailyStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final WatchSessionRepository watchSessionRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final AnalyticsService analyticsService;

    @Transactional
    public void saveWatch(UUID movieId, Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        WatchSession session = WatchSession.builder()
                .user(user)
                .movie(movie)
                .startedAt(Instant.now())
                .watchSeconds(0)
                .completed(true)
                .build();

        watchSessionRepository.save(session);
        long movieMinutes = movie.getDurationMin();
        analyticsService.incrementDailyStats(
                movieId,
                1,      // views_count
                1,      // watch_count
                movieMinutes,
                0,
                0,
                0
        );
    }

    public List<HistoryResponse> getHistory(String email) {

        User user = userRepository.findByEmail(email).orElseThrow();

        return watchSessionRepository
                .findByUserOrderByStartedAtDesc(user)
                .stream()
                .map(ws -> new HistoryResponse(
                        ws.getMovie().getId(),
                        ws.getMovie().getTitle(),
                        ws.getMovie().getPosterUrl()
                ))
                .distinct()
                .toList();
    }
}
