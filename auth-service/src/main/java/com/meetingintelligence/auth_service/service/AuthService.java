package com.meetingintelligence.auth_service.service;


import com.meetingintelligence.auth_service.dto.LoginRequest;
import com.meetingintelligence.auth_service.dto.RegisterRequest;
import com.meetingintelligence.auth_service.dto.UserResponse;
import com.meetingintelligence.auth_service.entity.User;
import com.meetingintelligence.auth_service.exception.ApiException;
import com.meetingintelligence.auth_service.repository.UserRepository;
import com.meetingintelligence.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public void register(RegisterRequest r) {
        String email = r.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }
        User u = new User();
        u.setFullName(r.fullName().trim());
        u.setEmail(email);
        u.setPassword(encoder.encode(r.password()));
        users.save(u);
    }

    public String login(LoginRequest r) {
        User u = users.findByEmail(r.email().trim().toLowerCase())
                .filter(x -> encoder.matches(r.password(), x.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!u.isApproved()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Account pending admin approval");
        }
        return jwt.generate(u);
    }

    public List<UserResponse> pending() {
        return users.findByApprovedFalse().stream().map(UserResponse::from).toList();
    }

    public UserResponse approve(Long id) {
        User u = find(id);
        u.setApproved(true);
        return UserResponse.from(users.save(u));
    }

    public void reject(Long id) {
        User u = find(id);
        if (u.isApproved()) {
            throw new ApiException(HttpStatus.CONFLICT, "Only pending users can be rejected");
        }
        users.delete(u);
    }

    private User find(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }
}