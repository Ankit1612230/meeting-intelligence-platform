package com.meetingintelligence.auth_service.controller;

import com.meetingintelligence.auth_service.dto.UserResponse;
import com.meetingintelligence.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;

    @GetMapping("/pending")
    List<UserResponse> pending() {
        return authService.pending();
    }

    @PutMapping("/approve/{id}")
    UserResponse approve(@PathVariable Long id) {
        return authService.approve(id);
    }

    @DeleteMapping("/reject/{id}")
    ResponseEntity<Void> reject(@PathVariable Long id) {
        authService.reject(id);
        return ResponseEntity.noContent().build();
    }
}