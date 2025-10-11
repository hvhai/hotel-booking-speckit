package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.model.User;
import com.codehunter.hotelbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request.getUsername(), request.getPassword(), request.getEmail(), request.getMembershipLevel());
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/membership")
    public ResponseEntity<User> updateMembershipLevel(@PathVariable UUID userId, @RequestBody UpdateMembershipRequest request) {
        User user = userService.updateMembershipLevel(userId, request.getMembershipLevel());
        return ResponseEntity.ok(user);
    }

    public static class CreateUserRequest {
        private String username;
        private String password;
        private String email;
        private User.MembershipLevel membershipLevel;
        // getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public User.MembershipLevel getMembershipLevel() { return membershipLevel; }
        public void setMembershipLevel(User.MembershipLevel membershipLevel) { this.membershipLevel = membershipLevel; }
    }

    public static class UpdateMembershipRequest {
        private User.MembershipLevel membershipLevel;
        public User.MembershipLevel getMembershipLevel() { return membershipLevel; }
        public void setMembershipLevel(User.MembershipLevel membershipLevel) { this.membershipLevel = membershipLevel; }
    }
}

