package com.codehunter.hotelbooking.service;

import com.codehunter.hotelbooking.model.User;
import com.codehunter.hotelbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    public User createUser(String username, String password, String email, User.MembershipLevel membershipLevel) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // In production, encode password!
        user.setEmail(email);
        user.setMembershipLevel(membershipLevel);
        user.setCreatedAt(java.time.Instant.now());
        user.setUpdatedAt(java.time.Instant.now());
        return userRepository.save(user);
    }

    public User updateMembershipLevel(java.util.UUID userId, User.MembershipLevel membershipLevel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setMembershipLevel(membershipLevel);
        user.setUpdatedAt(java.time.Instant.now());
        return userRepository.save(user);
    }
}
