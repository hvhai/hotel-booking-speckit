package com.codehunter.hotelbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BookingResponse represents the invoice/summary for a booking, including itemized discount details.
 * <p>
 * Fields:
 * <ul>
 *   <li>totalAmount: The original total price before discount</li>
 *   <li>discountAmount: The discount applied based on membership level</li>
 *   <li>finalAmount: The final price after discount</li>
 * </ul>
 */
public class BookingResponse {
    /** Unique identifier for the booking */
    private UUID bookingId;
    /** Unique identifier for the user */
    private UUID userId;
    /** Unique identifier for the room */
    private UUID roomId;
    /** Check-in date and time */
    private LocalDateTime checkIn;
    /** Check-out date and time */
    private LocalDateTime checkOut;
    /** Membership level at time of booking */
    private MembershipLevel membershipLevel;
    /**
     * The original total price before any discount is applied.
     */
    private BigDecimal totalAmount;
    /**
     * The discount amount applied based on the user's membership level.
     */
    private BigDecimal discountAmount;
    /**
     * The final price after the discount is applied.
     */
    private BigDecimal finalAmount;
    /** Booking status (ACTIVE or CANCELLED) */
    private Status status;

    public enum MembershipLevel {
        CLASSIC, GOLD, DIAMOND
    }

    public enum Status {
        ACTIVE, CANCELLED
    }

    public UUID getBookingId() {
        return bookingId;
    }
    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public UUID getRoomId() {
        return roomId;
    }
    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }
    public LocalDateTime getCheckIn() {
        return checkIn;
    }
    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }
    public LocalDateTime getCheckOut() {
        return checkOut;
    }
    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }
    public MembershipLevel getMembershipLevel() {
        return membershipLevel;
    }
    public void setMembershipLevel(MembershipLevel membershipLevel) {
        this.membershipLevel = membershipLevel;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    public BigDecimal getFinalAmount() {
        return finalAmount;
    }
    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}
