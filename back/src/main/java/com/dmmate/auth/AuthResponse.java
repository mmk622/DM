package com.dmmate.auth;

public record AuthResponse(
                String accessToken,
                String refreshToken,
                boolean profileIncomplete) {
}