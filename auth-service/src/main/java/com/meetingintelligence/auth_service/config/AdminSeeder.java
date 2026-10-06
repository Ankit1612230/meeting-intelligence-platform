package com.meetingintelligence.auth_service.config;

import com.meetingintelligence.auth_service.entity.Role;
import com.meetingintelligence.auth_service.entity.User;
import com.meetingintelligence.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    @Override
    public void run(ApplicationArguments args) {
        if (users.existsByRole(Role.ADMIN)) return;

        User admin = new User();
        admin.setFullName("Admin");
        admin.setEmail(email.toLowerCase());
        admin.setPassword(encoder.encode(password));
        admin.setRole(Role.ADMIN);
        admin.setApproved(true);
        users.save(admin);
    }
}