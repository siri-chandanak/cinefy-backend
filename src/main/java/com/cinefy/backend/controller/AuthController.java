package com.cinefy.backend.controller;

import com.cinefy.backend.dto.*;
import com.cinefy.backend.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/me")
    public String me(Authentication authentication) {
        return authentication.getName(); // email
    }
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest req)
    {
        return authService.register(req);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
