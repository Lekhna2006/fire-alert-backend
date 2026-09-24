package com.firealert.backend.controller;

import com.firealert.backend.dto.LoginRequest;
import com.firealert.backend.dto.RegisterRequest;
import com.firealert.backend.model.User;
import com.firealert.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/profile/{deviceId}")
    public User getProfile(@PathVariable String deviceId) {
        return userService.getProfile(deviceId);
    }
}