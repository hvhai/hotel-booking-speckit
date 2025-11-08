package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.TestContainerConfig;
import com.codehunter.hotelbooking.dto.RoomResponse;
import com.codehunter.hotelbooking.service.RoomService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainerConfig.class)
@ActiveProfiles("gemini")
class RoomControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RoomService roomService;

    @Test
    void getRooms_WithoutDates_ShouldReturnAllRooms() throws Exception {
        // Given
        RoomResponse room1 = createRoomResponse("101", "DELUXE", BigDecimal.valueOf(150.00));
        RoomResponse room2 = createRoomResponse("102", "STANDARD", BigDecimal.valueOf(100.00));
        List<RoomResponse> rooms = Arrays.asList(room1, room2);

        when(roomService.convertToRoomResponses(Mockito.any())).thenReturn(rooms);

        // When & Then
        mockMvc.perform(get("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].roomNumber").value("101"))
                .andExpect(jsonPath("$[0].type").value("DELUXE"))
                .andExpect(jsonPath("$[0].pricePerNight").value(150.00))
                .andExpect(jsonPath("$[1].roomNumber").value("102"))
                .andExpect(jsonPath("$[1].type").value("STANDARD"))
                .andExpect(jsonPath("$[1].pricePerNight").value(100.00));
    }

    @Test
    void getRooms_WithValidDates_ShouldReturnAvailableRooms() throws Exception {
        // Given
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);
        RoomResponse room1 = createRoomResponse("201", "SUITE", BigDecimal.valueOf(250.00));
        List<RoomResponse> availableRooms = Arrays.asList(room1);

        when(roomService.getAvailableRooms(fromDate, toDate)).thenReturn(availableRooms);

        // When & Then
        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].roomNumber").value("201"))
                .andExpect(jsonPath("$[0].type").value("SUITE"))
                .andExpect(jsonPath("$[0].pricePerNight").value(250.00));
    }

    @Test
    void getRooms_WithValidDates_NoAvailableRooms_ShouldReturnEmptyList() throws Exception {
        // Given
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);
        List<RoomResponse> availableRooms = Collections.emptyList();

        when(roomService.getAvailableRooms(fromDate, toDate)).thenReturn(availableRooms);

        // When & Then
        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRooms_WithOnlyFromDate_ShouldReturnBadRequest() throws Exception {
        // Given
        LocalDate fromDate = LocalDate.now().plusDays(1);

        // When & Then
        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", fromDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRooms_WithOnlyToDate_ShouldReturnBadRequest() throws Exception {
        // Given
        LocalDate toDate = LocalDate.now().plusDays(3);

        // When & Then
        mockMvc.perform(get("/api/v1/rooms")
                        .param("toDate", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRooms_WithInvalidDateFormat_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", "2024-12-25T14:30:00") // Invalid format - should be yyyy-MM-dd
                        .param("toDate", "2024-12-27")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRooms_WithPastDate_ShouldReturnBadRequest() throws Exception {
        // Given
        LocalDate fromDate = LocalDate.now().minusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);

        when(roomService.getAvailableRooms(fromDate, toDate))
                .thenThrow(new IllegalArgumentException("fromDate cannot be in the past"));

        // When & Then
        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRooms_WithFromDateAfterToDate_ShouldReturnBadRequest() throws Exception {
        // Given
        LocalDate fromDate = LocalDate.now().plusDays(5);
        LocalDate toDate = LocalDate.now().plusDays(3);

        when(roomService.getAvailableRooms(fromDate, toDate))
                .thenThrow(new IllegalArgumentException("fromDate must be before or equal to toDate"));

        // When & Then
        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRooms_WithSameDates_ShouldReturnAvailableRooms() throws Exception {
        // Given
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(1);
        RoomResponse room1 = createRoomResponse("301", "DELUXE", BigDecimal.valueOf(175.00));
        List<RoomResponse> availableRooms = Arrays.asList(room1);

        when(roomService.getAvailableRooms(fromDate, toDate)).thenReturn(availableRooms);

        // When & Then
        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].roomNumber").value("301"));
    }

    private RoomResponse createRoomResponse(String roomNumber, String type, BigDecimal pricePerNight) {
        RoomResponse room = new RoomResponse();
        room.setId(UUID.randomUUID());
        room.setRoomNumber(roomNumber);
        room.setType(type);
        room.setPricePerNight(pricePerNight);
        return room;
    }
}