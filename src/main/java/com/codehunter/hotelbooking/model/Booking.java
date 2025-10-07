package com.codehunter.hotelbooking.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private Instant checkIn;

    @Column(nullable = false)
    private Instant checkOut;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    private Instant createdAt;
    private Instant updatedAt;

    public enum Status {
        ACTIVE, CANCELLED
    }

    // Getters and setters
    // ...
}

