package com.cinefy.backend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RecommendationQueryService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> getRecommendations(UUID userId, int limit) {

        String sql = """
        WITH pref_genres AS (
            SELECT genre_id, weight_score
            FROM user_preferences
            WHERE user_id = :userId
            ORDER BY weight_score DESC
            LIMIT 5
        ),
        watched AS (
            SELECT DISTINCT movie_id
            FROM watch_sessions
            WHERE user_id = :userId
        ),
        trending AS (
            SELECT movie_id,
                   (SUM(views_count) * 1.0 + SUM(likes_count) * 2.0 + SUM(watch_minutes) * 0.2) AS trend_score
            FROM movie_daily_stats
            WHERE day >= CURRENT_DATE - INTERVAL '7 days'
            GROUP BY movie_id
        ),
        candidates AS (
            SELECT mg.movie_id,
                   (pg.weight_score * 10.0) AS pref_score
            FROM movie_genres mg
            JOIN pref_genres pg ON pg.genre_id = mg.genre_id
        )
        SELECT c.movie_id,
               (c.pref_score + COALESCE(t.trend_score, 0)) AS score
        FROM candidates c
        JOIN movies m ON m.id = c.movie_id AND m.is_active = TRUE
        LEFT JOIN trending t ON t.movie_id = c.movie_id
        LEFT JOIN watched w ON w.movie_id = c.movie_id
        WHERE w.movie_id IS NULL
        GROUP BY c.movie_id, c.pref_score, t.trend_score
        ORDER BY score DESC
        LIMIT :limit
        """;

        return entityManager
                .createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("limit", limit)
                .getResultList();
    }
}
