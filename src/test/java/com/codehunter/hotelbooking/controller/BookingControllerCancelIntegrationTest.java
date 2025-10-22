package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.TestContainerConfig;
import com.codehunter.hotelbooking.dto.BookingRequest;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainerConfig.class)
@ActiveProfiles("gemini")
@Transactional
class BookingControllerCancelIntegrationTest {
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
    private UUID bookingId;

    @BeforeEach
    void setUp() throws Exception {
        room = new Room();
        room.setRoomNumber("CancelTest101");
        room.setType("STANDARD");
        room.setPricePerNight(BigDecimal.valueOf(100.00));
        roomRepository.save(room);

        user = new User();
        user.setUsername("canceluser");
        user.setEmail("cancel@example.com");
        user.setPassword("password");
        user.setMembershipLevel(User.MembershipLevel.CLASSIC);
        userRepository.save(user);

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(room.getId());
        bookingRequest.setCheckIn(Instant.now().plusSeconds(60 * 60 * 72)); // 72h from now
        bookingRequest.setCheckOut(Instant.now().plusSeconds(60 * 60 * 96)); // 96h from now

        MvcResult result = mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest))
                        .with(request -> {
                            request.setRemoteUser("canceluser");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        bookingId = objectMapper.readTree(content).get("bookingId").traverse(objectMapper).readValueAs(UUID.class);
    }

    @Test
    @WithMockUser(username = "canceluser")
    void testCancelBooking_FullRefund() throws Exception {
        mockMvc.perform(post("/api/v1/bookings/" + bookingId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refundAmount").value(100.0))
                .andExpect(jsonPath("$.penaltyAmount").value(0.0))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Full refund")));
    }

    @Test
    @WithMockUser(username = "canceluser")
    void testCancelBooking_AlreadyCancelled() throws Exception {
        // First cancel
        mockMvc.perform(post("/api/v1/bookings/" + bookingId + "/cancel"))
                .andExpect(status().isOk());
        // Second cancel should fail
        mockMvc.perform(post("/api/v1/bookings/" + bookingId + "/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}

