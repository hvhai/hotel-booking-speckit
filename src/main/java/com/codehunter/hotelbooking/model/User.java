package com.codehunter.hotelbooking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipLevel membershipLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    private Instant createdAt;
    private Instant updatedAt;

    // Getters and setters
    // ...

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public MembershipLevel getMembershipLevel() {
        return membershipLevel;
    }

    public Role getRole() {
        return role;
    }

    public enum MembershipLevel {
        CLASSIC, GOLD, DIAMOND
    }

    public enum Role {
        USER, ADMIN
    }
}
