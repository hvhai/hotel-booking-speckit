package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.TestContainerConfig;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainerConfig.class)
@Transactional
@ActiveProfiles("gemini")
class RoomControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private Room room1;
    private Room room2;
    private Room room3;
    private User user;

    @BeforeEach
    void setUp() {
        // Create test user
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setMembershipLevel(User.MembershipLevel.CLASSIC);
        userRepository.save(user);

        // Use existing rooms from ApplicationBootstrapper
        room1 = roomRepository.findByRoomNumber("101").orElseThrow();
        room2 = roomRepository.findByRoomNumber("102").orElseThrow();
        room3 = roomRepository.findByRoomNumber("103").orElseThrow();
    }

    @Test
    void getRooms_WithoutDates_ShouldReturnAllRooms() throws Exception {
        mockMvc.perform(get("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5)) // 5 rooms from ApplicationBootstrapper
                .andExpect(jsonPath("$[*].roomNumber").value(org.hamcrest.Matchers.containsInAnyOrder("101", "102", "103", "104", "105")))
                .andExpect(jsonPath("$[*].type").value(org.hamcrest.Matchers.containsInAnyOrder("Single", "Double", "Suite", "Deluxe", "Family")));
    }

    @Test
    void getRooms_WithNoBookings_ShouldReturnAllRooms() throws Exception {
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);

        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5)) // All 5 rooms from ApplicationBootstrapper are available
                .andExpect(jsonPath("$[*].roomNumber").value(org.hamcrest.Matchers.containsInAnyOrder("101", "102", "103", "104", "105")));
    }

    @Test
    void getRooms_WithActiveBooking_ShouldReturnAvailableRooms() throws Exception {
        // Create a booking for room1 that overlaps with the requested dates
        LocalDate fromDate = LocalDate.now().plusDays(5);
        LocalDate toDate = LocalDate.now().plusDays(8);

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime bookingStart = fromDate.minusDays(1).atStartOfDay(zoneId);
        ZonedDateTime bookingEnd = fromDate.plusDays(2).atStartOfDay(zoneId);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room1);
        booking.setCheckIn(bookingStart.toInstant());
        booking.setCheckOut(bookingEnd.toInstant());
        booking.setTotalAmount(BigDecimal.valueOf(300.00));
        booking.setDiscountAmount(BigDecimal.ZERO);
        booking.setFinalAmount(BigDecimal.valueOf(300.00));
        booking.setStatus(Booking.Status.ACTIVE);
        bookingRepository.save(booking);

        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4)) // 5 total rooms - 1 booked room
                .andExpect(jsonPath("$[*].roomNumber").value(org.hamcrest.Matchers.containsInAnyOrder("102", "103", "104", "105")))
                .andExpect(jsonPath("$[*].roomNumber").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.contains("101"))));
    }

    @Test
    void getRooms_WithCancelledBooking_ShouldReturnAllRooms() throws Exception {
        // Create a cancelled booking for room1
        LocalDate fromDate = LocalDate.now().plusDays(5);
        LocalDate toDate = LocalDate.now().plusDays(8);

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime bookingStart = fromDate.minusDays(1).atStartOfDay(zoneId);
        ZonedDateTime bookingEnd = fromDate.plusDays(2).atStartOfDay(zoneId);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room1);
        booking.setCheckIn(bookingStart.toInstant());
        booking.setCheckOut(bookingEnd.toInstant());
        booking.setTotalAmount(BigDecimal.valueOf(300.00));
        booking.setDiscountAmount(BigDecimal.ZERO);
        booking.setFinalAmount(BigDecimal.valueOf(300.00));
        booking.setStatus(Booking.Status.CANCELLED);
        bookingRepository.save(booking);

        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5)) // Cancelled bookings don't block availability
                .andExpect(jsonPath("$[*].roomNumber").value(org.hamcrest.Matchers.containsInAnyOrder("101", "102", "103", "104", "105")));
    }

    @Test
    void getRooms_WithNonOverlappingBooking_ShouldReturnAllRooms() throws Exception {
        // Create a booking that doesn't overlap with the requested dates
        LocalDate bookingDate = LocalDate.now().plusDays(10);
        LocalDate requestedFromDate = LocalDate.now().plusDays(5);
        LocalDate requestedToDate = LocalDate.now().plusDays(8);

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime bookingStart = bookingDate.atStartOfDay(zoneId);
        ZonedDateTime bookingEnd = bookingDate.plusDays(2).atStartOfDay(zoneId);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room1);
        booking.setCheckIn(bookingStart.toInstant());
        booking.setCheckOut(bookingEnd.toInstant());
        booking.setTotalAmount(BigDecimal.valueOf(300.00));
        booking.setDiscountAmount(BigDecimal.ZERO);
        booking.setFinalAmount(BigDecimal.valueOf(300.00));
        booking.setStatus(Booking.Status.ACTIVE);
        bookingRepository.save(booking);

        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", requestedFromDate.toString())
                        .param("toDate", requestedToDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5)) // Non-overlapping booking doesn't block availability
                .andExpect(jsonPath("$[*].roomNumber").value(org.hamcrest.Matchers.containsInAnyOrder("101", "102", "103", "104", "105")));
    }

    @Test
    void getRooms_WithMultipleOverlappingBookings_ShouldReturnAvailableRooms() throws Exception {
        LocalDate fromDate = LocalDate.now().plusDays(5);
        LocalDate toDate = LocalDate.now().plusDays(8);

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime bookingStart = fromDate.minusDays(1).atStartOfDay(zoneId);
        ZonedDateTime bookingEnd = fromDate.plusDays(2).atStartOfDay(zoneId);

        // Create overlapping bookings for room1 and room2
        Booking booking1 = createBooking(user, room1, bookingStart.toInstant(), bookingEnd.toInstant());
        bookingRepository.save(booking1);

        Booking booking2 = createBooking(user, room2, bookingStart.toInstant(), bookingEnd.toInstant());
        bookingRepository.save(booking2);

        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3)) // 5 total rooms - 2 booked rooms
                .andExpect(jsonPath("$[*].roomNumber").value(org.hamcrest.Matchers.containsInAnyOrder("103", "104", "105")))
                .andExpect(jsonPath("$[*].roomNumber").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsInAnyOrder("101", "102"))));
    }

    @Test
    void getRooms_WithOnlyFromDate_ShouldReturnBadRequest() throws Exception {
        LocalDate fromDate = LocalDate.now().plusDays(1);

        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", fromDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRooms_WithOnlyToDate_ShouldReturnBadRequest() throws Exception {
        LocalDate toDate = LocalDate.now().plusDays(3);

        mockMvc.perform(get("/api/v1/rooms")
                        .param("toDate", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRooms_WithPastDate_ShouldReturnBadRequest() throws Exception {
        LocalDate fromDate = LocalDate.now().minusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(3);

        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRooms_WithInvalidDateFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/rooms")
                        .param("fromDate", "2024-12-25T14:30:00") // Invalid format
                        .param("toDate", "2024-12-27")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private Booking createBooking(User user, Room room, Instant checkIn, Instant checkOut) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setTotalAmount(BigDecimal.valueOf(300.00));
        booking.setDiscountAmount(BigDecimal.ZERO);
        booking.setFinalAmount(BigDecimal.valueOf(300.00));
        booking.setStatus(Booking.Status.ACTIVE);
        return booking;
    }
}