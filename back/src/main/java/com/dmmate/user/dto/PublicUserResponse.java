package com.dmmate.user.dto;

import com.dmmate.user.User;

public record PublicUserResponse(
    String email,
    String nickname,
    String name,
    Double avgPostRating,
    Long postCount) {
  public static PublicUserResponse of(User u, Double avg, Long count) {
    return new PublicUserResponse(
        u.getEmail(),
        u.getNickname(),
        u.getName(),
        normalizeAvg(avg),
        count == null ? 0L : count);
  }

  // 과거 호환: 평균/개수 없으면 0으로
  public static PublicUserResponse of(User u) {
    return of(u, 0.0, 0L);
  }

  private static Double normalizeAvg(Double v) {
    if (v == null)
      return 0.0;
    // 소수 첫째자리 반올림 (원하면 그대로 평균을 내려도 됨)
    return Math.round(v * 10.0) / 10.0;
  }
}