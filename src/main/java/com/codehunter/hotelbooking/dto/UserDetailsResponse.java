package com.codehunter.hotelbooking.dto;

public class UserDetailsResponse {
    private String username;
    private String email;
    private String role;
    private String membershipLevel;

    public UserDetailsResponse(String username, String email, String role, String membershipLevel) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.membershipLevel = membershipLevel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMembershipLevel() {
        return membershipLevel;
    }

    public void setMembershipLevel(String membershipLevel) {
        this.membershipLevel = membershipLevel;
    }
}
