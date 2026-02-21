package com.cinefy.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(unique=true, nullable = false)
    private String email;

    @Column(name="password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role", columnDefinition = "user_role")
    private Role role;

    private Integer age;
    private String gender;
    private String country;
    private String city;

    @Column(name="language_pref")
    private String languagePref;

    @Column(name="movies_watched_count")
    private Long moviesWatchedCount;

    @Column(name="likes_count")
    private Long likesCount;

    @Column(name="total_watched_minutes")
    private Long totalWatchMinutes;
}