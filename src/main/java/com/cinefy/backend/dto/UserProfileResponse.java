package com.cinefy.backend.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String name;
    private String email;
    private Integer age;
    private String country;
    private String city;
    private String languagePref;
}
