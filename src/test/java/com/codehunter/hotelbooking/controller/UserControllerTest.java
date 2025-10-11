package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.model.User;
import com.codehunter.hotelbooking.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testuser")
    void getMembershipInfo_shouldReturnMembershipDetails() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setMembershipLevel(User.MembershipLevel.GOLD);
        Mockito.when(userService.findByUsername(anyString())).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/me/membership")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.membershipLevel").value("GOLD"))
                .andExpect(jsonPath("$.discountRate").value(0.10))
                .andExpect(jsonPath("$.allLevels.CLASSIC").value(0.0))
                .andExpect(jsonPath("$.allLevels.GOLD").value(0.10))
                .andExpect(jsonPath("$.allLevels.DIAMOND").value(0.20));
    }
}

