package com.cinefy.backend.repository;

import com.cinefy.backend.model.Movie;
import com.cinefy.backend.model.MovieDailyStats;
import com.cinefy.backend.model.MovieDailyStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

public interface MovieDailyStatsRepository extends JpaRepository<MovieDailyStats, MovieDailyStatsId> {

    @Query(value = """
    SELECT movie_id,
           (views_count * 1 +
            watch_count * 3 +
            likes_count * 4 +
            ratings_count * 2 +
            reviews_count * 3) AS score
    FROM movie_daily_stats
    WHERE day = CURRENT_DATE
    ORDER BY score DESC
    LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> getTodayTrending(@Param("limit") int limit);


    @Modifying
    @Query(value = """
    INSERT INTO movie_daily_stats
    (movie_id, day, views_count, watch_count, watch_minutes,
     likes_count, ratings_count, reviews_count, updated_at)
    VALUES (:movieId, :day, :views, :watchCount, :watchMinutes,
            :likes, :ratings, :reviews, NOW())
    ON CONFLICT (movie_id, day)
    DO UPDATE SET
        views_count = movie_daily_stats.views_count + EXCLUDED.views_count,
        watch_count = movie_daily_stats.watch_count + EXCLUDED.watch_count,
        watch_minutes = movie_daily_stats.watch_minutes + EXCLUDED.watch_minutes,
        likes_count = movie_daily_stats.likes_count + EXCLUDED.likes_count,
        ratings_count = movie_daily_stats.ratings_count + EXCLUDED.ratings_count,
        reviews_count = movie_daily_stats.reviews_count + EXCLUDED.reviews_count,
        updated_at = NOW()
    """, nativeQuery = true)
    void upsertDailyStats(
            @Param("movieId") UUID movieId,
            @Param("day") LocalDate day,
            @Param("views") long views,
            @Param("watchCount") long watchCount,
            @Param("watchMinutes") long watchMinutes,
            @Param("likes") long likes,
            @Param("ratings") long ratings,
            @Param("reviews") long reviews
    );
}
