package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.dto.BookingRequest;
import com.codehunter.hotelbooking.dto.BookingResponse;
import com.codehunter.hotelbooking.model.User;
import com.codehunter.hotelbooking.service.BookingService;
import com.codehunter.hotelbooking.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private BookingService bookingService;
    @MockitoBean
    private UserService userService;

    private BookingRequest bookingRequest;
    private User appUser;
    private BookingResponse bookingResponse;

    @BeforeEach
    void setUp() {
        bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(UUID.randomUUID());
        bookingRequest.setCheckIn(Instant.parse("2025-10-10T14:00:00Z"));
        bookingRequest.setCheckOut(Instant.parse("2025-10-12T12:00:00Z"));

        appUser = new User();
        appUser.setId(UUID.randomUUID());
        appUser.setUsername("testuser");
        appUser.setMembershipLevel(User.MembershipLevel.CLASSIC);

        bookingResponse = new BookingResponse();
        bookingResponse.setBookingId(UUID.randomUUID());
        bookingResponse.setUserId(appUser.getId());
        bookingResponse.setRoomId(bookingRequest.getRoomId());
        bookingResponse.setCheckIn(bookingRequest.getCheckIn());
        bookingResponse.setCheckOut(bookingRequest.getCheckOut());
        bookingResponse.setMembershipLevel(BookingResponse.MembershipLevel.CLASSIC);
        bookingResponse.setTotalAmount(BigDecimal.valueOf(200));
        bookingResponse.setDiscountAmount(BigDecimal.ZERO);
        bookingResponse.setFinalAmount(BigDecimal.valueOf(200));
        bookingResponse.setStatus(BookingResponse.Status.ACTIVE);
    }

    @Test
    @WithMockUser(username = "testuser")
    void createBooking_shouldReturnBookingResponse() throws Exception {
        when(userService.findByUsername(eq("testuser"))).thenReturn(appUser);
        when(bookingService.createBooking(any(BookingRequest.class), eq(appUser))).thenReturn(bookingResponse);

        mockMvc.perform(post("/api/v1/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(appUser.getId().toString()))
                .andExpect(jsonPath("$.roomId").value(bookingRequest.getRoomId().toString()))
                .andExpect(jsonPath("$.totalAmount").value(200))
                .andExpect(jsonPath("$.discountAmount").value(0))
                .andExpect(jsonPath("$.finalAmount").value(200))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}

