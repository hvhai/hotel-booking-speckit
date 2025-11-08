package com.codehunter.hotelbooking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Getter
@Setter
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String roomNumber;

    private String type;

    @Column(nullable = false)
    private BigDecimal pricePerNight;

    // Getters and setters
    // ...

    @Override
    public String toString() {
        return "Room{" +
               "id=" + id +
               ", roomNumber='" + roomNumber + '\'' +
               ", type='" + type + '\'' +
               ", pricePerNight=" + pricePerNight +
               '}';
    }
}

