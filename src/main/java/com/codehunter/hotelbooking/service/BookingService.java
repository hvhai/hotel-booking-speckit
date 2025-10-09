package com.codehunter.hotelbooking.service;

import com.codehunter.hotelbooking.dto.BookingRequest;
import com.codehunter.hotelbooking.dto.BookingResponse;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

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
        // Calculate nights
        long nights = Duration.between(
                request.getCheckIn().atZone(ZoneOffset.UTC).toInstant(),
                request.getCheckOut().atZone(ZoneOffset.UTC).toInstant()
        ).toDays();
        if (nights <= 0) throw new IllegalArgumentException("Check-out must be after check-in");
        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        BigDecimal discountPercent = getDiscountPercent(user.getMembershipLevel());
        BigDecimal discountAmount = totalAmount.multiply(discountPercent);
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckIn(request.getCheckIn().atZone(ZoneOffset.UTC).toInstant());
        booking.setCheckOut(request.getCheckOut().atZone(ZoneOffset.UTC).toInstant());
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
}

