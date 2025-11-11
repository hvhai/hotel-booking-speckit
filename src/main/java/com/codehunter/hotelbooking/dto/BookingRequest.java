package com.codehunter.hotelbooking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    @NotNull
    private UUID roomId;
    @NotNull
    private Instant checkIn;
    @NotNull
    private Instant checkOut;

    public UUID getRoomId() {
        return roomId;
    }
    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }
    public Instant getCheckIn() {
        return checkIn;
    }
    public void setCheckIn(Instant checkIn) {
        this.checkIn = checkIn;
    }
    public Instant getCheckOut() {
        return checkOut;
    }
    public void setCheckOut(Instant checkOut) {
        this.checkOut = checkOut;
    }
}
