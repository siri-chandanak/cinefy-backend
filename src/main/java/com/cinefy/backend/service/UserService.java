package com.cinefy.backend.service;

import com.cinefy.backend.dto.UserProfileResponse;
import com.cinefy.backend.model.User;
import com.cinefy.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileResponse res = new UserProfileResponse();
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setCountry(user.getCountry());
        res.setCity(user.getCity());
        res.setLanguagePref(user.getLanguagePref());

        return res;
    }
}
