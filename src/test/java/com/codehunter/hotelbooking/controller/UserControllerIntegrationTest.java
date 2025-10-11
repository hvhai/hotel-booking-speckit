package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.TestContainerConfig;
import com.codehunter.hotelbooking.model.User;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainerConfig.class)
@Transactional
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("integrationuser");
        user.setEmail("integration@example.com");
        user.setPassword("password");
        user.setMembershipLevel(User.MembershipLevel.DIAMOND);
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "integrationuser")
    void getMembershipInfo_shouldReturnDiamondLevel() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/membership")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.membershipLevel").value("DIAMOND"))
                .andExpect(jsonPath("$.discountRate").value(0.20))
                .andExpect(jsonPath("$.allLevels.CLASSIC").value(0.0))
                .andExpect(jsonPath("$.allLevels.GOLD").value(0.10))
                .andExpect(jsonPath("$.allLevels.DIAMOND").value(0.20));
    }
}

