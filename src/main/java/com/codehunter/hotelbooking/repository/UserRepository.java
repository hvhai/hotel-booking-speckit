package com.codehunter.hotelbooking.repository;

import com.codehunter.hotelbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

