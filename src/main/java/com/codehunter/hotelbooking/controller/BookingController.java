package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.dto.BookingRequest;
import com.codehunter.hotelbooking.dto.BookingResponse;
import com.codehunter.hotelbooking.service.BookingService;
import com.codehunter.hotelbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bookings")
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
        // Retrieve username from Spring Security User
        String username = user.getUsername();
        // Fetch application User from database using UserService
        com.codehunter.hotelbooking.model.User appUser = userService.findByUsername(username);
        BookingResponse response = bookingService.createBooking(request, appUser);
        return ResponseEntity.ok(response);
    }
}
