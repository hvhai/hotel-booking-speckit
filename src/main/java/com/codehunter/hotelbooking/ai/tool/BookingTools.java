package com.codehunter.hotelbooking.ai.tool;

import com.codehunter.hotelbooking.model.Booking;
import com.codehunter.hotelbooking.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BookingTools {
    private final BookingService bookingService;

    @Tool(description = "Get booking details by booking id")
    public BookingDetailsResponse getBookingDetails(@ToolParam(description = "The user booking id in UUID format") String bookingId) {
        Booking bookingById = bookingService.getBookingById(UUID.fromString(bookingId));
        LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
        return new BookingDetailsResponse(bookingById.getId(),
                bookingById.getUser().getUsername(),
                bookingById.getCheckIn().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString(),
                bookingById.getCheckOut().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString());
    }

    @Tool(description = "Cancel booking by booking id")
    public void cancelBooking(@ToolParam(description = "The user booking id in UUID format") String bookingId) {
        bookingService.cancelBooking(UUID.fromString(bookingId), Instant.now());
    }

    public record BookingDetailsResponse(UUID bookingId, String username, String checkIn, String checkOut) {
    }
}
