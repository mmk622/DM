package com.dmmate.auth.dto;

import com.dmmate.user.User;

public record AuthUserDto(
        Long id,
        String email,
        String name,
        String nickname,
        boolean incomplete // true면 추가정보 필요
) {
    public static AuthUserDto from(User u) {
        boolean inc = (u.getName() == null || u.getNickname() == null || u.getPasswordHash() == null);
        return new AuthUserDto(
                u.getId(),
                u.getEmail(),
                u.getName(),
                u.getNickname(),
                inc
        );
    }
}