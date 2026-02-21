package com.cinefy.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;

    private Integer age;
    private String gender;
    private String country;
    private String city;
    private String languagePref;

    private List<String> genres; // ['Action', 'Sci-Fi']
}