package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.model.User;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    private User adminUser;

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
}
