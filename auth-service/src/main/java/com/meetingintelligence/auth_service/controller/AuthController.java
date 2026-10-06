package com.meetingintelligence.auth_service.controller;

import com.meetingintelligence.auth_service.dto.LoginRequest;
import com.meetingintelligence.auth_service.dto.RegisterRequest;
import com.meetingintelligence.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest r) {
        authService.register(r);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Registered. Please wait for admin approval."));
    }

    @PostMapping("/login")
    Map<String, String> login(@Valid @RequestBody LoginRequest r) {
        return Map.of("token", authService.login(r));
    }
}