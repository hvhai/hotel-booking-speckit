package com.codehunter.hotelbooking.service;

import com.codehunter.hotelbooking.dto.BookingRequest;
import com.codehunter.hotelbooking.dto.BookingResponse;
import com.codehunter.hotelbooking.model.Booking;
import com.codehunter.hotelbooking.model.Room;
import com.codehunter.hotelbooking.model.User;
import com.codehunter.hotelbooking.model.User.MembershipLevel;
import com.codehunter.hotelbooking.repository.BookingRepository;
import com.codehunter.hotelbooking.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomRepository roomRepository;
    @InjectMocks
    private BookingService bookingService;

    private Room room;
    private User user;
    private BookingRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        room = new Room();
        room.setId(UUID.randomUUID());
        room.setPricePerNight(BigDecimal.valueOf(100));
        user = new User();
        user.setId(UUID.randomUUID());
        request = new BookingRequest();
        request.setRoomId(room.getId());
        request.setCheckIn(LocalDateTime.of(2025, 10, 10, 14, 0));
        request.setCheckOut(LocalDateTime.of(2025, 10, 12, 12, 0)); // 2 nights
    }

    @Test
    void testBookingWithClassicMembership() {
        user.setMembershipLevel(MembershipLevel.CLASSIC);
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        BookingResponse response = bookingService.createBooking(request, user);
        assertEquals(BigDecimal.valueOf(200), response.getTotalAmount());
        assertEquals(BigDecimal.ZERO, response.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(200), response.getFinalAmount());
    }

    @Test
    void testBookingWithGoldMembership() {
        user.setMembershipLevel(MembershipLevel.GOLD);
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        BookingResponse response = bookingService.createBooking(request, user);
        assertEquals(BigDecimal.valueOf(200), response.getTotalAmount());
        assertEquals(BigDecimal.valueOf(20.0).setScale(2), response.getDiscountAmount().setScale(2));
        assertEquals(BigDecimal.valueOf(180.0).setScale(2), response.getFinalAmount().setScale(2));
    }

    @Test
    void testBookingWithDiamondMembership() {
        user.setMembershipLevel(MembershipLevel.DIAMOND);
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        BookingResponse response = bookingService.createBooking(request, user);
        assertEquals(BigDecimal.valueOf(200), response.getTotalAmount());
        assertEquals(BigDecimal.valueOf(40.0).setScale(2), response.getDiscountAmount().setScale(2));
        assertEquals(BigDecimal.valueOf(160.0).setScale(2), response.getFinalAmount().setScale(2));
    }
}

