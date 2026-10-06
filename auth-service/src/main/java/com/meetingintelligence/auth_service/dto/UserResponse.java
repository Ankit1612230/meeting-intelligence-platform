package com.meetingintelligence.auth_service.dto;

import com.meetingintelligence.auth_service.entity.Role;
import com.meetingintelligence.auth_service.entity.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        boolean approved,
        LocalDateTime createdAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                u.getRole(),
                u.isApproved(),
                u.getCreatedAt()
        );
    }
}