package com.cinefy.backend.repository;

import com.cinefy.backend.model.UserPreference;
import com.cinefy.backend.model.UserPreferenceKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, UserPreferenceKey>{
}
