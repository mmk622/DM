package com.dmmate.auth.dto;

import com.dmmate.user.User;

public record AuthUserDto(
    Long id,
    String email,
    String name,
    String nickname,
    boolean profileIncomplete
) {
    public static AuthUserDto from(User u) {
        return new AuthUserDto(
            u.getId(),
            u.getEmail(),
            u.getName(),
            u.getNickname(),
            (u.getName() == null || u.getNickname() == null || u.getPasswordHash() == null)
        );
    }

    public static AuthUserDto incomplete(String email) {
        return new AuthUserDto(null, email, null, null, true);
    }
}