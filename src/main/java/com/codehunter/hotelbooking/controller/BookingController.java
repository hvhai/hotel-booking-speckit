package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.dto.BookingRequest;
import com.codehunter.hotelbooking.dto.BookingResponse;
import com.codehunter.hotelbooking.dto.CancellationResponse;
import com.codehunter.hotelbooking.dto.RefundPreviewResponse;
import com.codehunter.hotelbooking.service.BookingService;
import com.codehunter.hotelbooking.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@Slf4j
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal User user
    ) {
        log.info("Creating a new booking for user {}", user);
        // Retrieve username from Spring Security User
        String username = user.getUsername();
        // Fetch application User from database using UserService
        com.codehunter.hotelbooking.model.User appUser = userService.findByUsername(username);
        BookingResponse response = bookingService.createBooking(request, appUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<CancellationResponse> cancelBooking(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal User user
    ) {
        // Enforce booking ownership: only the owner can cancel
        String username = user.getUsername();
        com.codehunter.hotelbooking.model.User appUser = userService.findByUsername(username);
        // Fetch booking and check ownership
        com.codehunter.hotelbooking.model.Booking booking = bookingService.getBookingById(bookingId);
        if (!booking.getUser().getId().equals(appUser.getId())) {
            // Optionally log unauthorized attempt
            log.warn("User {} attempted to cancel booking {} not owned by them", username, bookingId);
            return ResponseEntity.status(403).build(); // Forbidden
        }
        CancellationResponse response = bookingService.cancelBooking(bookingId, Instant.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}/refund-preview")
    public ResponseEntity<RefundPreviewResponse> previewRefund(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal User user
    ) {
        // Optionally, you could check if the booking belongs to the user here
        RefundPreviewResponse response = bookingService.previewRefund(bookingId, Instant.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal User user) {
        String username = user.getUsername();
        com.codehunter.hotelbooking.model.User appUser = userService.findByUsername(username);
        List<BookingResponse> bookings = bookingService.getBookingsForUser(appUser.getId());
        return ResponseEntity.ok(bookings);
    }
}
