package com.codehunter.hotelbooking.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cancellations")
public class Cancellation {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(nullable = false)
    private Instant cancelledAt;

    @Column(nullable = false)
    private BigDecimal refundAmount;

    @Column(nullable = false)
    private BigDecimal penaltyAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus refundStatus = RefundStatus.PENDING;

    public enum RefundStatus {
        PENDING, COMPLETED, FAILED
    }

    // Getters and setters
    // ...
}

