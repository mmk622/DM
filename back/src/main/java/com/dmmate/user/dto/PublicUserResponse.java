package com.dmmate.user.dto;

import com.dmmate.user.User;

public record PublicUserResponse(
    String email,
    String nickname,
    String name) {
  public static PublicUserResponse of(User u) {
    return new PublicUserResponse(
        u.getEmail(),
        u.getNickname(),
        u.getName());
  }
}