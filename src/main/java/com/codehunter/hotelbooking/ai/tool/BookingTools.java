package com.codehunter.hotelbooking.ai.tool;

import com.codehunter.hotelbooking.dto.BookingRequest;
import com.codehunter.hotelbooking.dto.BookingResponse;
import com.codehunter.hotelbooking.model.Booking;
import com.codehunter.hotelbooking.model.User;
import com.codehunter.hotelbooking.service.BookingService;
import com.codehunter.hotelbooking.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class BookingTools {
    private final BookingService bookingService;
    private final UserService userService;

    @Tool(description = "Get booking details by booking id")
    public BookingDetailsResponse getBookingDetails(@ToolParam(description = "The user booking id in UUID format") String bookingId) {
        log.info("Get booking details by booking id {}", bookingId);
        Booking bookingById = bookingService.getBookingById(UUID.fromString(bookingId));
        LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
        return new BookingDetailsResponse(bookingById.getId(),
                bookingById.getUser().getUsername(),
                bookingById.getCheckIn().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString(),
                bookingById.getCheckOut().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString());
    }

    @Tool(description = "Cancel booking by booking id")
    public void cancelBooking(@ToolParam(description = "The user booking id in UUID format") String bookingId) {
        log.info("Cancel booking by booking id {}", bookingId);
        bookingService.cancelBooking(UUID.fromString(bookingId), Instant.now());
    }

    @Tool(description = "Create a new booking for a user")
    public BookingDetailsResponse createBooking(@ToolParam(description = "The user name") String username,
                                                @ToolParam(description = "The room id in UUID format") String roomId,
                                                @ToolParam(description = "The check-in date in ISO-8601 format") String checkIn,
                                                @ToolParam(description = "The check-out date in ISO-8601 format") String checkOut) {
        log.info("Create a new booking for user {} with details roomId {}, checkIn {}, checkOut {} ", username, roomId, checkIn, checkOut);
        User appUser = userService.findByUsername(username);
        BookingResponse booking = bookingService.createBooking(
                new BookingRequest(UUID.fromString(roomId),
                        LocalDate.parse(checkIn).atTime(7,0,0).atZone(LocaleContextHolder.getTimeZone().toZoneId()).toInstant(),
                        LocalDate.parse(checkOut).atTime(7,0,0).atZone(LocaleContextHolder.getTimeZone().toZoneId()).toInstant()),
                appUser
        );
        return new BookingDetailsResponse(booking.getBookingId(),
                username,
                booking.getCheckIn().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString(),
                booking.getCheckOut().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString());
    }

    public record BookingDetailsResponse(UUID bookingId, String username, String checkIn, String checkOut) {
    }
}
