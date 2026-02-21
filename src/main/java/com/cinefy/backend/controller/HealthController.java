package com.cinefy.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class HealthController {
    @GetMapping("/health")
    public String checkHealth(){
        return "Cinefy backend is running";
    }
}
