package com.codehunter.hotelbooking.controller;

import com.codehunter.hotelbooking.dto.MembershipInfoResponse;
import com.codehunter.hotelbooking.model.User;
import com.codehunter.hotelbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/membership")
    public ResponseEntity<MembershipInfoResponse> getMembershipInfo(@AuthenticationPrincipal UserDetails principal) {
        String username = principal.getUsername();
        User appUser = userService.findByUsername(username);
        MembershipInfoResponse response = MembershipInfoResponse.fromUser(appUser);
        return ResponseEntity.ok(response);
    }
}

