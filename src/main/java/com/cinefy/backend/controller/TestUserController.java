package com.cinefy.backend.controller;

import com.cinefy.backend.model.Role;
import org.springframework.web.bind.annotation.*;
import com.cinefy.backend.model.User;
import com.cinefy.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestUserController {

    private final UserRepository userRepository;

    @PostMapping("/create-user")
    public User createUser() {
        User user=User.builder()
                .name("Test User")
                .email("Test@example.com")
                .passwordHash("123456")
                .role(Role.USER)
                .age(25)
                .country("USA")
                .languagePref("English")
                .moviesWatchedCount(0L)
                .likesCount(0L)
                .totalWatchMinutes(0L)
                .build();
        return userRepository.save(user);
    }
}
