package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.model.Booking;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;

    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        // Create an admin user in DB (simulate, as actual security config may vary)
        adminUser = new User();
        adminUser.setUsername("adminTest");
        adminUser.setPassword("adminpass");
        adminUser.setEmail("admin-test@example.com");
        adminUser.setMembershipLevel(User.MembershipLevel.DIAMOND);
        adminUser.setRole(User.Role.ADMIN);
        userRepository.save(adminUser);
        // Create a regular user
        regularUser = new User();
        regularUser.setUsername("userTest");
        regularUser.setPassword("userpass");
        regularUser.setEmail("user-test@example.com");
        regularUser.setMembershipLevel(User.MembershipLevel.CLASSIC);
        regularUser.setRole(User.Role.USER);
        userRepository.save(regularUser);
    }

    @Test
    @WithMockUser(username = "adminTest", roles = {"ADMIN"})
    void testAdminCanCreateUser() throws Exception {
        String req = "{" +
                     "\"username\":\"newuser\"," +
                     "\"password\":\"newpass\"," +
                     "\"email\":\"newuser@example.com\"," +
                     "\"membershipLevel\":\"GOLD\"}";
        String response = mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        User created = objectMapper.readValue(response, User.class);
        assertEquals("newuser", created.getUsername());
        assertEquals(User.MembershipLevel.GOLD, created.getMembershipLevel());
        assertNotNull(created.getId());
    }

    @Test
    @WithMockUser(username = "adminTest", roles = {"ADMIN"})
    void testAdminCanUpdateMembership() throws Exception {
        // Create a user to update
        User user = new User();
        user.setUsername("updateuser");
        user.setPassword("pass");
        user.setEmail("updateuser@example.com");
        user.setMembershipLevel(User.MembershipLevel.CLASSIC);
        userRepository.save(user);
        String req = "{" +
                     "\"membershipLevel\":\"DIAMOND\"}";
        String response = mockMvc.perform(put("/api/v1/admin/users/" + user.getId() + "/membership")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        User updated = objectMapper.readValue(response, User.class);
        assertEquals(User.MembershipLevel.DIAMOND, updated.getMembershipLevel());
    }

    @Test
    @WithMockUser(username = "adminTest", roles = {"ADMIN"})
    void testAdminCanListAllBookings() throws Exception {
        // Arrange: create a room
        Room room = new Room();
        room.setRoomNumber("AdminTest103");
        room.setType("STANDARD");
        room.setPricePerNight(BigDecimal.valueOf(100));
        room = roomRepository.save(room);
        // Arrange: create a booking in the DB for a regular user with all required fields
        Booking booking = new Booking();
        booking.setUser(regularUser);
        booking.setRoom(room);
        booking.setCheckIn(Instant.now().plus(1, ChronoUnit.DAYS));
        booking.setCheckOut(Instant.now().plus(2, ChronoUnit.DAYS));
        booking.setTotalAmount(BigDecimal.valueOf(100));
        booking.setDiscountAmount(BigDecimal.ZERO);
        booking.setFinalAmount(BigDecimal.valueOf(100));
        booking.setStatus(Booking.Status.ACTIVE);
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);
        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/users/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").exists())
                .andExpect(jsonPath("$[0].roomId").value(room.getId().toString()))
                .andExpect(jsonPath("$[0].userId").value(regularUser.getId().toString()));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testNonAdminCannotCreateUser() throws Exception {
        String req = "{" +
                     "\"username\":\"failuser\"," +
                     "\"password\":\"failpass\"," +
                     "\"email\":\"failuser@example.com\"," +
                     "\"membershipLevel\":\"GOLD\"}";
        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testNonAdminCannotUpdateMembership() throws Exception {
        User user = new User();
        user.setUsername("failupdate");
        user.setPassword("pass");
        user.setEmail("failupdate@example.com");
        user.setMembershipLevel(User.MembershipLevel.CLASSIC);
        userRepository.save(user);
        String req = "{" +
                     "\"membershipLevel\":\"DIAMOND\"}";
        mockMvc.perform(put("/api/v1/admin/users/" + user.getId() + "/membership")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "userTest", roles = {"USER"})
    void testNonAdminCannotListAllBookings() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
