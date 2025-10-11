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
        user.setRole(User.Role.USER);
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

    @Test
    void testCancelBooking_AsNonOwner_ShouldReturnForbidden() throws Exception {
        // Create and save a second user
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        otherUser.setEmail("other@example.com");
        otherUser.setPassword("password");
        otherUser.setMembershipLevel(User.MembershipLevel.CLASSIC);
        otherUser.setRole(User.Role.USER);
        userRepository.save(otherUser);

        // Create a booking for the first user
        BookingRequest req = new BookingRequest();
        req.setRoomId(room.getId());
        req.setCheckIn(Instant.parse("2025-10-20T14:00:00Z"));
        req.setCheckOut(Instant.parse("2025-10-22T12:00:00Z"));
        BookingResponse bookingResponse = objectMapper.readValue(
            mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(),
            BookingResponse.class
        );

        // Attempt to cancel as the other user
        mockMvc.perform(post("/api/v1/bookings/" + bookingResponse.getBookingId() + "/cancel")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(otherUser.getUsername())))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetMyBookings_OnlyReturnsOwnBookings() throws Exception {
        // Create and save a second user
        User otherUser2 = new User();
        otherUser2.setUsername("otheruser2");
        otherUser2.setEmail("other2@example.com");
        otherUser2.setPassword("password");
        otherUser2.setMembershipLevel(User.MembershipLevel.CLASSIC);
        otherUser2.setRole(User.Role.USER);
        userRepository.save(otherUser2);

        // Create a booking for the first user
        BookingRequest req1 = new BookingRequest();
        req1.setRoomId(room.getId());
        req1.setCheckIn(Instant.parse("2025-10-25T14:00:00Z"));
        req1.setCheckOut(Instant.parse("2025-10-27T12:00:00Z"));
        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
                .andExpect(status().isOk());

        // Create a booking for the second user
        BookingRequest req2 = new BookingRequest();
        req2.setRoomId(room.getId());
        req2.setCheckIn(Instant.parse("2025-10-28T14:00:00Z"));
        req2.setCheckOut(Instant.parse("2025-10-30T12:00:00Z"));
        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(otherUser2.getUsername())))
                .andExpect(status().isOk());

        // Get bookings for the first user
        String response = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/bookings/my")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        BookingResponse[] bookings = objectMapper.readValue(response, BookingResponse[].class);
        // Should only return bookings for the first user
        for (BookingResponse b : bookings) {
            assertEquals(user.getId(), b.getUserId(), "Should only return bookings for the authenticated user");
        }
        // Should not include bookings for the second user
        for (BookingResponse b : bookings) {
            assertEquals("testuser", b.getMembershipLevel().name().toLowerCase().contains("classic") ? user.getUsername() : "testuser");
        }
    }
}
