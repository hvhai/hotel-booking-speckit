package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.TestContainerConfig;
import com.codehunter.hotelbooking.dto.BookingRequest;
import com.codehunter.hotelbooking.dto.BookingResponse;
import com.codehunter.hotelbooking.model.Room;
import com.codehunter.hotelbooking.model.User;
import com.codehunter.hotelbooking.repository.BookingRepository;
import com.codehunter.hotelbooking.repository.RoomRepository;
import com.codehunter.hotelbooking.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainerConfig.class)
@Transactional
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Room room;
    private User user;
    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        // Create and save a test room
        room = new Room();
        room.setRoomNumber("IntTest101");
        room.setType("STANDARD");
        room.setPricePerNight(BigDecimal.valueOf(100.00));
        Room roomStored = roomRepository.save(room);

        // Create and save a test user
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password"); // In real scenario, this should be encoded
        user.setMembershipLevel(User.MembershipLevel.CLASSIC);
        userRepository.save(user);

        // Create a booking request
        bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(roomStored.getId());
        bookingRequest.setCheckIn(Instant.parse("2025-10-15T14:00:00Z"));
        bookingRequest.setCheckOut(Instant.parse("2025-10-17T12:00:00Z")); // 2 nights
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateBooking_Success() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists())
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.roomId").value(room.getId().toString()))
                .andExpect(jsonPath("$.checkIn").exists())
                .andExpect(jsonPath("$.checkOut").exists())
                .andExpect(jsonPath("$.membershipLevel").value("CLASSIC"))
                .andExpect(jsonPath("$.totalAmount").value(200.00))
                .andExpect(jsonPath("$.discountAmount").value(0.00))
                .andExpect(jsonPath("$.finalAmount").value(200.00))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        BookingResponse response = objectMapper.readValue(content, BookingResponse.class);

        assertNotNull(response.getBookingId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(room.getId(), response.getRoomId());
        assertEquals(BookingResponse.MembershipLevel.CLASSIC, response.getMembershipLevel());
        assertEquals(BigDecimal.valueOf(200.00).setScale(2), response.getTotalAmount().setScale(2));
        assertEquals(BigDecimal.valueOf(0.00).setScale(2), response.getDiscountAmount().setScale(2));
        assertEquals(BigDecimal.valueOf(200.00).setScale(2), response.getFinalAmount().setScale(2));
        assertEquals(BookingResponse.Status.ACTIVE, response.getStatus());
        
        // Assert that Booking is stored in DB
        boolean bookingExists = bookingRepository.findById(response.getBookingId()).isPresent();
        assertEquals(true, bookingExists, "Booking should be created in the database");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateBooking_InvalidDates() throws Exception {
        // Set check-out before check-in
        bookingRequest.setCheckIn(Instant.parse("2025-10-15T14:00:00Z"));
        bookingRequest.setCheckOut(Instant.parse("2025-10-15T12:00:00Z")); // Same day, earlier time

        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isBadRequest());
    }
}
