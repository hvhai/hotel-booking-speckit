package com.codehunter.hotelbooking.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CancellationResponse {
    private UUID bookingId;
    private BigDecimal refundAmount;
    private BigDecimal penaltyAmount;
    private String message;

    public UUID getBookingId() {
        return bookingId;
    }
    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }
    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }
    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}

