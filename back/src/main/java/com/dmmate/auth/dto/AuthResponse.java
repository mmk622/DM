package com.dmmate.auth.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken) {
}