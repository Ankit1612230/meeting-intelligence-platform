package com.meetingintelligence.auth_service.repository;

import com.meetingintelligence.auth_service.entity.Role;
import com.meetingintelligence.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    List<User> findByApprovedFalse();
}