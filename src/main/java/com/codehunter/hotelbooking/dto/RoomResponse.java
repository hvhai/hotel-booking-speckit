package com.codehunter.hotelbooking.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * RoomResponse represents the room details for API responses.
 * <p>
 * Fields:
 * <ul>
 *   <li>id: Unique identifier for the room</li>
 *   <li>roomNumber: Unique room number identifier</li>
 *   <li>type: Room type/category</li>
 *   <li>pricePerNight: Price per night for the room</li>
 * </ul>
 */
public class RoomResponse {
    /** Unique identifier for the room */
    private UUID id;
    /** Unique room number identifier */
    private String roomNumber;
    /** Room type/category */
    private String type;
    /** Price per night for the room */
    private BigDecimal pricePerNight;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
}