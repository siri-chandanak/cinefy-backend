package com.cinefy.backend.service;

import com.cinefy.backend.repository.MovieDailyStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final MovieDailyStatsRepository movieDailyStatsRepository;

    @Transactional
    public void incrementDailyStats(
            UUID movieId,
            long views,
            long watchCount,
            long watchMinutes,
            long likes,
            long ratings,
            long reviews
    ) {
        movieDailyStatsRepository.upsertDailyStats(
                movieId,
                LocalDate.now(),
                views,
                watchCount,
                watchMinutes,
                likes,
                ratings,
                reviews
        );
    }
}
