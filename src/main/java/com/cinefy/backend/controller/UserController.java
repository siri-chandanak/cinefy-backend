package com.cinefy.backend.controller;

import com.cinefy.backend.dto.UserProfileResponse;
import com.cinefy.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileResponse getProfile(Authentication authentication) {
        String email = authentication.getName();
        return userService.getCurrentUser(email);
    }
}