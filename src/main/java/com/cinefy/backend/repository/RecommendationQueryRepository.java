package com.cinefy.backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RecommendationQueryRepository {

    @PersistenceContext
    private EntityManager em;
    public List<Long> findTopGenreIdsForUser(UUID userId, int limit) {

        String sql = """
        SELECT mg.genre_id, SUM(score) AS total_score
        FROM (
            -- Watched movies weight = 3
            SELECT ws.movie_id, 3 AS score
            FROM watch_sessions ws
            WHERE ws.user_id = :userId

            UNION ALL

            -- Liked movies weight = 5
            SELECT ml.movie_id, 5 AS score
            FROM movie_likes ml
            WHERE ml.user_email = (
                SELECT email FROM users WHERE id = :userId
            )
        ) x
        JOIN movie_genres mg ON mg.movie_id = x.movie_id
        GROUP BY mg.genre_id
        ORDER BY total_score DESC
        LIMIT :limit
    """;

        List<?> rows = em.createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("limit", limit)
                .getResultList();

        List<Long> genreIds = new ArrayList<>();
        for (Object rowObj : rows) {
            Object[] row = (Object[]) rowObj;
            genreIds.add(((Number) row[0]).longValue());
        }
        return genreIds;
    }

    /**
     * Content-based: movies that match top genres, excluding already watched.
     */
    public List<UUID> findMoviesByGenresExcludingWatched(UUID userId, List<Long> genreIds, int limit) {

        if (genreIds == null || genreIds.isEmpty()) return Collections.emptyList();
        String sql = """
            SELECT DISTINCT ON (m.id) m.id
                        FROM movies m
                        JOIN movie_genres mg ON mg.movie_id = m.id
                        WHERE mg.genre_id IN (:genreIds)
                          AND m.id NOT IN (
                              SELECT ws.movie_id FROM watch_sessions ws WHERE ws.user_id = :userId
                          )
                        ORDER BY m.id, m.total_views DESC NULLS LAST
                        LIMIT :limit;
                        
        """;


        @SuppressWarnings("unchecked")
        List<Object> ids = em.createNativeQuery(sql)
                .setParameter("genreIds", genreIds)
                .setParameter("userId", userId)
                .setParameter("limit", limit)
                .getResultList();

        return ids.stream().map(this::toUUID).collect(Collectors.toList());
    }

    /**
     * Similar users: users who watched the same movies (top N).
     */
    public List<UUID> findSimilarUsers(UUID userId, int limit) {

        String sql = """
            SELECT ws2.user_id, COUNT(*) AS similarity
            FROM watch_sessions ws1
            JOIN watch_sessions ws2 ON ws1.movie_id = ws2.movie_id
            WHERE ws1.user_id = :userId
              AND ws2.user_id <> :userId
            GROUP BY ws2.user_id
            ORDER BY similarity DESC
            LIMIT :limit
        """;

        List<?> rows = em.createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("limit", limit)
                .getResultList();

        List<UUID> users = new ArrayList<>();
        for (Object rowObj : rows) {
            Object[] row = (Object[]) rowObj;
            users.add(toUUID(row[0]));
        }
        return users;
    }

    /**
     * Collaborative: recommend movies watched by similar users, excluding already watched by current user.
     */
    public List<UUID> findMoviesWatchedByUsersExcludingWatched(UUID userId, List<UUID> similarUsers, int limit) {

        if (similarUsers == null || similarUsers.isEmpty()) return Collections.emptyList();

        String sql = """
            SELECT ws.movie_id, COUNT(*) AS score
            FROM watch_sessions ws
            WHERE ws.user_id IN (:similarUsers)
              AND ws.movie_id NOT IN (
                  SELECT movie_id FROM watch_sessions WHERE user_id = :userId
              )
            GROUP BY ws.movie_id
            ORDER BY score DESC
            LIMIT :limit
        """;

        List<?> rows = em.createNativeQuery(sql)
                .setParameter("similarUsers", similarUsers)
                .setParameter("userId", userId)
                .setParameter("limit", limit)
                .getResultList();

        List<UUID> movieIds = new ArrayList<>();
        for (Object rowObj : rows) {
            Object[] row = (Object[]) rowObj;
            movieIds.add(toUUID(row[0]));
        }
        return movieIds;
    }

    /**
     * Trending fallback.
     * Uses movie_daily_stats(movie_id, views_count) OR movies.total_views.
     */
    public List<UUID> findTrendingMovieIds(int limit) {

        // Prefer daily stats if present. If not, fallback to movies.total_views.
        String sql = """
            SELECT m.id
            FROM movies m
            LEFT JOIN movie_daily_stats s ON s.movie_id = m.id
            ORDER BY COALESCE(s.views_count, m.total_views, 0) DESC
            LIMIT :limit
        """;

        @SuppressWarnings("unchecked")
        List<Object> ids = em.createNativeQuery(sql)
                .setParameter("limit", limit)
                .getResultList();

        return ids.stream().map(this::toUUID).collect(Collectors.toList());
    }

    private UUID toUUID(Object o) {
        if (o == null) return null;
        if (o instanceof UUID u) return u;
        return UUID.fromString(o.toString());
    }
}
