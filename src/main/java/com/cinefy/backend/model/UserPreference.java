package com.cinefy.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {
    @EmbeddedId
    private UserPreferenceKey id;

    @Column(name = "weight_score", nullable = false)
    private Integer weightScore;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
