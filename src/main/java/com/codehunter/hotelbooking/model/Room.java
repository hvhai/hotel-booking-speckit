package com.codehunter.hotelbooking.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String roomNumber;

    private String type;

    @Column(nullable = false)
    private BigDecimal pricePerNight;

    // Getters and setters
    // ...
}

