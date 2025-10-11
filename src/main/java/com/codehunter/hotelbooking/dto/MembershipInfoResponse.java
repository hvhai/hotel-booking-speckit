package com.codehunter.hotelbooking.dto;

import com.codehunter.hotelbooking.model.User;
import java.util.LinkedHashMap;
import java.util.Map;

public class MembershipInfoResponse {
    private String membershipLevel;
    private double discountRate;
    private Map<String, Double> allLevels;

    public static MembershipInfoResponse fromUser(User user) {
        MembershipInfoResponse resp = new MembershipInfoResponse();
        resp.membershipLevel = user.getMembershipLevel().name();
        resp.discountRate = getDiscountForLevel(user.getMembershipLevel().name());
        resp.allLevels = new LinkedHashMap<>();
        resp.allLevels.put("CLASSIC", 0.0);
        resp.allLevels.put("GOLD", 0.10);
        resp.allLevels.put("DIAMOND", 0.20);
        return resp;
    }

    private static double getDiscountForLevel(String level) {
        switch (level) {
            case "GOLD": return 0.10;
            case "DIAMOND": return 0.20;
            default: return 0.0;
        }
    }

    public String getMembershipLevel() { return membershipLevel; }
    public double getDiscountRate() { return discountRate; }
    public Map<String, Double> getAllLevels() { return allLevels; }
}

