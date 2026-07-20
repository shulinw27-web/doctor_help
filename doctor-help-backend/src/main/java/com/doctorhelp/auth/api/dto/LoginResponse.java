package com.doctorhelp.auth.api.dto;

public record LoginResponse(String accessToken, UserProfile user) {
    public record UserProfile(String id, String username, String name, String role, String departmentName, String title) {
    }
}

