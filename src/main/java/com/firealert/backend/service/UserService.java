package com.firealert.backend.service;

import com.firealert.backend.dto.LoginRequest;
import com.firealert.backend.dto.RegisterRequest;
import com.firealert.backend.model.User;
import com.firealert.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Hash the password before storing it
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setDeviceId(request.getDeviceId());

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {

        User user = userRepository
                .findByEmailAndDeviceId(
                        request.getEmail(),
                        request.getDeviceId()
                )
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or device ID"));

        // Compare entered password with stored BCrypt hash
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    public User getProfile(String email, String deviceId) {

        return userRepository
                .findByEmailAndDeviceId(email, deviceId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
}