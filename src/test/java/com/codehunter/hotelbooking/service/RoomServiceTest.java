package com.codehunter.hotelbooking.service;

import com.codehunter.hotelbooking.dto.RoomResponse;
import com.codehunter.hotelbooking.model.Room;
import com.codehunter.hotelbooking.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;
    @InjectMocks
    private RoomService roomService;

    private Room room1;
    private Room room2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        room1 = new Room();
        room1.setId(UUID.randomUUID());
        room1.setRoomNumber("101");
        room1.setType("DELUXE");
        room1.setPricePerNight(BigDecimal.valueOf(150.00));

        room2 = new Room();
        room2.setId(UUID.randomUUID());
        room2.setRoomNumber("102");
        room2.setType("STANDARD");
        room2.setPricePerNight(BigDecimal.valueOf(100.00));
    }

    @Test
    void testGetAllRooms() {
        // Given
        List<Room> expectedRooms = Arrays.asList(room1, room2);
        when(roomRepository.findAll()).thenReturn(expectedRooms);

        // When
        List<Room> actualRooms = roomService.getAllRooms();

        // Then
        assertEquals(expectedRooms, actualRooms);
        verify(roomRepository).findAll();
    }

    @Test
    void testGetAvailableRooms_Success() {
        // Given
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);
        List<Room> availableRooms = Arrays.asList(room1, room2);

        when(roomRepository.findAvailableRoomsInDateRange(any(), any())).thenReturn(availableRooms);

        // When
        List<RoomResponse> result = roomService.getAvailableRooms(fromDate, toDate);

        // Then
        assertEquals(2, result.size());

        RoomResponse response1 = result.get(0);
        assertEquals(room1.getId(), response1.getId());
        assertEquals(room1.getRoomNumber(), response1.getRoomNumber());
        assertEquals(room1.getType(), response1.getType());
        assertEquals(room1.getPricePerNight(), response1.getPricePerNight());

        RoomResponse response2 = result.get(1);
        assertEquals(room2.getId(), response2.getId());
        assertEquals(room2.getRoomNumber(), response2.getRoomNumber());
        assertEquals(room2.getType(), response2.getType());
        assertEquals(room2.getPricePerNight(), response2.getPricePerNight());

        verify(roomRepository).findAvailableRoomsInDateRange(any(), any());
    }

    @Test
    void testGetAvailableRooms_NoAvailableRooms() {
        // Given
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);
        List<Room> availableRooms = Collections.emptyList();

        when(roomRepository.findAvailableRoomsInDateRange(any(), any())).thenReturn(availableRooms);

        // When
        List<RoomResponse> result = roomService.getAvailableRooms(fromDate, toDate);

        // Then
        assertTrue(result.isEmpty());
        verify(roomRepository).findAvailableRoomsInDateRange(any(), any());
    }

    @Test
    void testGetAvailableRooms_NullFromDate() {
        // Given
        LocalDate fromDate = null;
        LocalDate toDate = LocalDate.now().plusDays(3);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> roomService.getAvailableRooms(fromDate, toDate)
        );
        assertEquals("Both fromDate and toDate must be provided", exception.getMessage());
        verify(roomRepository, never()).findAvailableRoomsInDateRange(any(), any());
    }

    @Test
    void testGetAvailableRooms_NullToDate() {
        // Given
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> roomService.getAvailableRooms(fromDate, toDate)
        );
        assertEquals("Both fromDate and toDate must be provided", exception.getMessage());
        verify(roomRepository, never()).findAvailableRoomsInDateRange(any(), any());
    }

    @Test
    void testGetAvailableRooms_FromDateAfterToDate() {
        // Given
        LocalDate fromDate = LocalDate.now().plusDays(3);
        LocalDate toDate = LocalDate.now().plusDays(1);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> roomService.getAvailableRooms(fromDate, toDate)
        );
        assertEquals("fromDate must be before or equal to toDate", exception.getMessage());
        verify(roomRepository, never()).findAvailableRoomsInDateRange(any(), any());
    }

    @Test
    void testGetAvailableRooms_FromDateInPast() {
        // Given
        LocalDate fromDate = LocalDate.now().minusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> roomService.getAvailableRooms(fromDate, toDate)
        );
        assertEquals("fromDate cannot be in the past", exception.getMessage());
        verify(roomRepository, never()).findAvailableRoomsInDateRange(any(), any());
    }

    @Test
    void testGetAvailableRooms_SameDates() {
        // Given
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(1);
        List<Room> availableRooms = Arrays.asList(room1);

        when(roomRepository.findAvailableRoomsInDateRange(any(), any())).thenReturn(availableRooms);

        // When
        List<RoomResponse> result = roomService.getAvailableRooms(fromDate, toDate);

        // Then
        assertEquals(1, result.size());
        assertEquals(room1.getId(), result.get(0).getId());
        verify(roomRepository).findAvailableRoomsInDateRange(any(), any());
    }

    @Test
    void testConvertToRoomResponses() {
        // Given
        List<Room> rooms = Arrays.asList(room1, room2);

        // When
        List<RoomResponse> result = roomService.convertToRoomResponses(rooms);

        // Then
        assertEquals(2, result.size());

        RoomResponse response1 = result.get(0);
        assertEquals(room1.getId(), response1.getId());
        assertEquals(room1.getRoomNumber(), response1.getRoomNumber());
        assertEquals(room1.getType(), response1.getType());
        assertEquals(room1.getPricePerNight(), response1.getPricePerNight());

        RoomResponse response2 = result.get(1);
        assertEquals(room2.getId(), response2.getId());
        assertEquals(room2.getRoomNumber(), response2.getRoomNumber());
        assertEquals(room2.getType(), response2.getType());
        assertEquals(room2.getPricePerNight(), response2.getPricePerNight());
    }

    @Test
    void testConvertToRoomResponses_EmptyList() {
        // Given
        List<Room> rooms = Collections.emptyList();

        // When
        List<RoomResponse> result = roomService.convertToRoomResponses(rooms);

        // Then
        assertTrue(result.isEmpty());
    }
}