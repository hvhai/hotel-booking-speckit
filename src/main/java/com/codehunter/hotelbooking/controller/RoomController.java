package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.dto.RoomResponse;
import com.codehunter.hotelbooking.model.Room;
import com.codehunter.hotelbooking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping("")
    public ResponseEntity<List<RoomResponse>> getRooms(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        List<RoomResponse> rooms;

        // If both dates are provided, return available rooms for that date range
        if (fromDate != null && toDate != null) {
            rooms = roomService.getAvailableRooms(fromDate, toDate);
        }
        // If no dates provided, return all rooms (backward compatibility)
        else if (fromDate == null && toDate == null) {
            List<Room> allRooms = roomService.getAllRooms();
            rooms = roomService.convertToRoomResponses(allRooms);
        }
        // If only one date is provided, it's invalid
        else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(rooms);
    }
}

