package com.cinefy.backend.service;

import com.cinefy.backend.dto.AuthResponse;
import com.cinefy.backend.dto.LoginRequest;
import com.cinefy.backend.dto.LoginResponse;
import com.cinefy.backend.dto.RegisterRequest;
import com.cinefy.backend.model.*;
import com.cinefy.backend.repository.*;
import com.cinefy.backend.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenreRepository genreRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest req)
    {
        userRepository.findByEmail(req.getEmail()).ifPresent(u ->{
            throw new RuntimeException("Email already registered");
        });
        String genrevCsv = (req.getEmail()==null || req.getGenres().isEmpty())
                ? null
                : req.getGenres().stream().collect(Collectors.joining(","));
        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.USER)
                .age(req.getAge())
                .gender(req.getGender())
                .country(req.getCountry())
                .city(req.getCity())
                .languagePref(req.getLanguagePref())
                .moviesWatchedCount(0L)
                .likesCount(0L)
                .totalWatchMinutes(0L)
                .build();
        userRepository.save(user);

        saveUserPreferences(user.getId(),req.getGenres());

        return new AuthResponse("Registered Successfully",user.getEmail(),user.getRole().name(),user.getId().toString());
    }

    public void saveUserPreferences(UUID userId, List<String>genres)
    {
        if(genres == null || genres.isEmpty()) return;

        int weight = (genres.size() == 1) ? 10 : 8;
        OffsetDateTime now = OffsetDateTime.now();

        for(String g: genres)
        {
            if(g==null) continue;
            String cleaned = g.trim();
            if(cleaned.isEmpty()) continue;
            //Ensure genre exists
            Genre genre = genreRepository.findByNameIgnoreCase(cleaned)
                    .orElseGet(() -> genreRepository.save(Genre.builder().name(cleaned).build()));

            //upsert user preference now
            UserPreferenceKey key = new UserPreferenceKey(userId, genre.getId());

            UserPreference pref = UserPreference.builder()
                    .id(key)
                    .weightScore(weight)
                    .updatedAt(now)
                    .build();

            userPreferenceRepository.save(pref);
        }

    }

    public LoginResponse login(LoginRequest req) {

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Email not found"
                        )
                );

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Incorrect password"
            );
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name()
        );
    }

}
