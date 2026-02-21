package com.cinefy.backend.repository;

import com.cinefy.backend.model.WatchSession;
import com.cinefy.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface WatchSessionRepository extends JpaRepository<WatchSession, UUID> {

    List<WatchSession> findByUserOrderByStartedAtDesc(User user);

    boolean existsByUserAndMovie_Id(User user, UUID movieId);

    Optional<WatchSession> findByUserAndMovie_Id(User user, UUID movieId);

    void deleteByUserAndMovie_Id(User user, UUID movieId);
}
