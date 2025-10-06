package com.dmmate.user.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MeResponse(
        String email,
        String name,
        String nickname,
        List<String> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}