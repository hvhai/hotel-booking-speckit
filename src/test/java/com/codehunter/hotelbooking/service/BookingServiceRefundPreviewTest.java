package com.codehunter.hotelbooking.service;

import com.codehunter.hotelbooking.dto.RefundPreviewResponse;
import com.codehunter.hotelbooking.model.Booking;
import com.codehunter.hotelbooking.model.Room;
import com.codehunter.hotelbooking.model.User;
import com.codehunter.hotelbooking.repository.BookingRepository;
import com.codehunter.hotelbooking.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceRefundPreviewTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomRepository roomRepository;
    @InjectMocks
    private BookingService bookingService;

    private Booking booking;
    private User user;
    private Room room;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("previewuser");
        user.setMembershipLevel(User.MembershipLevel.CLASSIC);
        room = new Room();
        room.setId(UUID.randomUUID());
        room.setPricePerNight(BigDecimal.valueOf(100));
        booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckIn(Instant.now().plusSeconds(60 * 60 * 72)); // 72h from now
        booking.setCheckOut(Instant.now().plusSeconds(60 * 60 * 96));
        booking.setTotalAmount(BigDecimal.valueOf(200));
        booking.setDiscountAmount(BigDecimal.ZERO);
        booking.setFinalAmount(BigDecimal.valueOf(200));
        booking.setStatus(Booking.Status.ACTIVE);
    }

    @Test
    void previewRefund_fullRefund() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        RefundPreviewResponse response = bookingService.previewRefund(booking.getId(), Instant.now());
        assertEquals(booking.getId(), response.getBookingId());
        assertEquals(BigDecimal.valueOf(200), response.getRefundAmount());
        assertEquals(BigDecimal.ZERO, response.getPenaltyAmount());
        assertTrue(response.getMessage().contains("Full refund"));
    }

    @Test
    void previewRefund_halfRefund() {
        booking.setCheckIn(Instant.now().plusSeconds(60 * 60 * 30)); // 30h from now
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        RefundPreviewResponse response = bookingService.previewRefund(booking.getId(), Instant.now());
        assertEquals(BigDecimal.valueOf(100.0), response.getRefundAmount());
        assertEquals(BigDecimal.valueOf(100.0), response.getPenaltyAmount());
        assertTrue(response.getMessage().contains("50% refund"));
    }

    @Test
    void previewRefund_noRefund() {
        booking.setCheckIn(Instant.now().plusSeconds(60 * 60 * 10)); // 10h from now
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        RefundPreviewResponse response = bookingService.previewRefund(booking.getId(), Instant.now());
        assertEquals(BigDecimal.ZERO, response.getRefundAmount());
        assertEquals(BigDecimal.valueOf(200), response.getPenaltyAmount());
        assertTrue(response.getMessage().contains("No refund"));
    }

    @Test
    void previewRefund_alreadyCancelled() {
        booking.setStatus(Booking.Status.CANCELLED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                bookingService.previewRefund(booking.getId(), Instant.now()));
        assertTrue(ex.getMessage().contains("already cancelled"));
    }

    @Test
    void previewRefund_afterCheckIn() {
        booking.setCheckIn(Instant.now().minusSeconds(60 * 60)); // 1h ago
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                bookingService.previewRefund(booking.getId(), Instant.now()));
        assertTrue(ex.getMessage().contains("Cannot preview refund after check-in time"));
    }
}

