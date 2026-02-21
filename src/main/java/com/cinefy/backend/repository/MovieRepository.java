package com.cinefy.backend.repository;

import com.cinefy.backend.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID>{
}

