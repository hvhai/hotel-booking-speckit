package com.codehunter.hotelbooking.service;

import com.codehunter.hotelbooking.dto.RoomResponse;
import com.codehunter.hotelbooking.model.Room;
import com.codehunter.hotelbooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * Gets available rooms for the specified date range.
     * A room is considered available if it has no ACTIVE bookings that overlap with the requested dates.
     *
     * @param fromDate The start date (inclusive)
     * @param toDate   The end date (exclusive)
     * @return List of available rooms
     */
    public List<RoomResponse> getAvailableRooms(LocalDate fromDate, LocalDate toDate) {
        // Validate dates
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Both fromDate and toDate must be provided");
        }
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("fromDate must be before or equal to toDate");
        }
        if (fromDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("fromDate cannot be in the past");
        }

        // Convert LocalDate to Instant at the start/end of day in system timezone
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime startDateTime = fromDate.atStartOfDay(zoneId);
        ZonedDateTime endDateTime = toDate.atStartOfDay(zoneId);

        Instant startDate = startDateTime.toInstant();
        Instant endDate = endDateTime.toInstant();

        // Get available rooms from repository
        List<Room> availableRooms = roomRepository.findAvailableRoomsInDateRange(startDate, endDate);

        // Convert to DTOs
        return availableRooms.stream()
                .map(this::convertToRoomResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Room entity to RoomResponse DTO.
     */
    private RoomResponse convertToRoomResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setType(room.getType());
        response.setPricePerNight(room.getPricePerNight());
        return response;
    }

    /**
     * Converts a list of Room entities to RoomResponse DTOs.
     */
    public List<RoomResponse> convertToRoomResponses(List<Room> rooms) {
        return rooms.stream()
                .map(this::convertToRoomResponse)
                .collect(Collectors.toList());
    }
}

