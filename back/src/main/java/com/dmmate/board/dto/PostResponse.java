package com.dmmate.board.dto;

import com.dmmate.board.domain.Post;
import com.dmmate.board.domain.GenderPref;
import com.dmmate.board.domain.PartyPref;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PostResponse(
    Long id,
    String authorId,
    String authorNickname,
    String title,
    String content,
    LocalDate mealDate,
    GenderPref genderPref,
    PartyPref partyPref,
    Double avgRating,
    Long ratingsCount,
    Integer myRating,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {
  public static PostResponse of(Post p, String nickname, Double avg, Long cnt, Integer my) {
    return new PostResponse(
        p.getId(), p.getAuthorId(), nickname, p.getTitle(), p.getContent(),
        p.getMealDate(), p.getGenderPref(), p.getPartyPref(), avg, cnt, my,
        p.getCreatedAt(), p.getUpdatedAt());
  }

  // 닉네임만 있는 간편 팩토리 (평점 정보 없음)
  public static PostResponse of(Post p, String nickname) {
    return of(p, nickname, null, null, null);
  }

  // 아무 부가정보도 없는 기본 팩토리 (기존 코드 호환)
  public static PostResponse of(Post p) {
    return of(p, null, null, null, null);
  }
}