package com.codehunter.hotelbooking.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
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

    private Instant createdAt;
    private Instant updatedAt;

    // Getters and setters
    // ...

    public enum MembershipLevel {
        CLASSIC, GOLD, DIAMOND
    }
}

