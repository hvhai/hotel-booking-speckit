package com.codehunter.hotelbooking.service;

import com.codehunter.hotelbooking.dto.BookingRequest;
import com.codehunter.hotelbooking.dto.BookingResponse;
import com.codehunter.hotelbooking.dto.CancellationResponse;
import com.codehunter.hotelbooking.dto.RefundPreviewResponse;
import com.codehunter.hotelbooking.model.Booking;
import com.codehunter.hotelbooking.model.Room;
import com.codehunter.hotelbooking.model.User;
import com.codehunter.hotelbooking.model.User.MembershipLevel;
import com.codehunter.hotelbooking.repository.BookingRepository;
import com.codehunter.hotelbooking.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;

    @Transactional
    public BookingResponse createBooking(BookingRequest request, User user) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        // Calculate nights (calendar days, ignore time)
        long nights = ChronoUnit.DAYS.between(request.getCheckIn().truncatedTo(ChronoUnit.DAYS), request.getCheckOut().truncatedTo(ChronoUnit.DAYS));
        if (nights <= 0) throw new IllegalArgumentException("Check-out must be after check-in");
        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        BigDecimal discountPercent = getDiscountPercent(user.getMembershipLevel());
        BigDecimal discountAmount = totalAmount.multiply(discountPercent);
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setTotalAmount(totalAmount);
        booking.setDiscountAmount(discountAmount);
        booking.setFinalAmount(finalAmount);
        booking.setStatus(Booking.Status.ACTIVE);
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());
        booking = bookingRepository.save(booking);
        // Build response
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getId());
        response.setUserId(user.getId());
        response.setRoomId(room.getId());
        response.setCheckIn(request.getCheckIn());
        response.setCheckOut(request.getCheckOut());
        response.setMembershipLevel(BookingResponse.MembershipLevel.valueOf(user.getMembershipLevel().name()));
        response.setTotalAmount(totalAmount);
        response.setDiscountAmount(discountAmount);
        response.setFinalAmount(finalAmount);
        response.setStatus(BookingResponse.Status.ACTIVE);
        return response;
    }

    private BigDecimal getDiscountPercent(MembershipLevel level) {
        switch (level) {
            case GOLD:
                return BigDecimal.valueOf(0.10);
            case DIAMOND:
                return BigDecimal.valueOf(0.20);
            default:
                return BigDecimal.ZERO;
        }
    }

    public CancellationResponse cancelBooking(UUID bookingId, Instant cancelTime) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }
        if (cancelTime == null) {
            cancelTime = Instant.now();
        }
        long hoursBeforeCheckIn = Duration.between(cancelTime, booking.getCheckIn()).toHours();
        BigDecimal refund;
        BigDecimal penalty;
        String message;
        if (hoursBeforeCheckIn > 48) {
            refund = booking.getFinalAmount();
            penalty = BigDecimal.ZERO;
            message = "Full refund (cancelled more than 48h before check-in)";
        } else if (hoursBeforeCheckIn >= 24) {
            refund = booking.getFinalAmount().multiply(BigDecimal.valueOf(0.5));
            penalty = booking.getFinalAmount().subtract(refund);
            message = "50% refund (cancelled 24-48h before check-in)";
        } else if (hoursBeforeCheckIn >= 0) {
            refund = BigDecimal.ZERO;
            penalty = booking.getFinalAmount();
            message = "No refund (cancelled less than 24h before check-in)";
        } else {
            throw new IllegalArgumentException("Cannot cancel after check-in time");
        }
        // Simulate refund processing if refund > 0
        if (refund.compareTo(BigDecimal.ZERO) > 0) {
            simulateRefundProcessing(booking, refund);
        }
        booking.setStatus(Booking.Status.CANCELLED);
        booking.setUpdatedAt(cancelTime);
        bookingRepository.save(booking);
        CancellationResponse response = new CancellationResponse();
        response.setBookingId(booking.getId());
        response.setRefundAmount(refund);
        response.setPenaltyAmount(penalty);
        response.setMessage(message);
        return response;
    }

    public RefundPreviewResponse previewRefund(UUID bookingId, Instant previewTime) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }
        if (previewTime == null) {
            previewTime = Instant.now();
        }
        long hoursBeforeCheckIn = Duration.between(previewTime, booking.getCheckIn()).toHours();
        BigDecimal refund;
        BigDecimal penalty;
        String message;
        if (hoursBeforeCheckIn > 48) {
            refund = booking.getFinalAmount();
            penalty = BigDecimal.ZERO;
            message = "Full refund (cancelled more than 48h before check-in)";
        } else if (hoursBeforeCheckIn >= 24) {
            refund = booking.getFinalAmount().multiply(BigDecimal.valueOf(0.5));
            penalty = booking.getFinalAmount().subtract(refund);
            message = "50% refund (cancelled 24-48h before check-in)";
        } else if (hoursBeforeCheckIn >= 0) {
            refund = BigDecimal.ZERO;
            penalty = booking.getFinalAmount();
            message = "No refund (cancelled less than 24h before check-in)";
        } else {
            throw new IllegalArgumentException("Cannot preview refund after check-in time");
        }
        RefundPreviewResponse response = new RefundPreviewResponse();
        response.setBookingId(booking.getId());
        response.setRefundAmount(refund);
        response.setPenaltyAmount(penalty);
        response.setMessage(message);
        return response;
    }

    private void simulateRefundProcessing(Booking booking, BigDecimal refundAmount) {
        // Simulate calling a payment gateway API for refund
        // In a real system, this would be an HTTP call or SDK integration
        // Here, we just log the action
        System.out.printf("Simulating refund of %s to user %s for booking %s\n", refundAmount, booking.getUser().getUsername(), booking.getId());
    }

    public List<BookingResponse> getBookingsForUser(UUID userId) {
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(b -> b.getUser().getId().equals(userId))
                .collect(Collectors.toList());
        return bookings.stream().map(b -> {
            BookingResponse r = new BookingResponse();
            r.setBookingId(b.getId());
            r.setUserId(b.getUser().getId());
            r.setRoomId(b.getRoom().getId());
            r.setCheckIn(b.getCheckIn());
            r.setCheckOut(b.getCheckOut());
            r.setMembershipLevel(BookingResponse.MembershipLevel.valueOf(b.getUser().getMembershipLevel().name()));
            r.setTotalAmount(b.getTotalAmount());
            r.setDiscountAmount(b.getDiscountAmount());
            r.setFinalAmount(b.getFinalAmount());
            r.setStatus(BookingResponse.Status.valueOf(b.getStatus().name()));
            return r;
        }).collect(Collectors.toList());
    }

    public com.codehunter.hotelbooking.model.Booking getBookingById(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
    }
}
