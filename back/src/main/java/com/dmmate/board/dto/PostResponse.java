package com.dmmate.board.dto;

import com.dmmate.board.domain.Post;
import com.dmmate.board.domain.GenderPref;
import com.dmmate.board.domain.PartyPref;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PostResponse(
    Long id,
    String authorId,
    String title,
    String content,
    LocalDate mealDate,
    GenderPref genderPref,
    PartyPref partyPref,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {
  public static PostResponse of(Post p) {
    return new PostResponse(
        p.getId(), p.getAuthorId(), p.getTitle(), p.getContent(),
        p.getMealDate(), p.getGenderPref(), p.getPartyPref(),
        p.getCreatedAt(), p.getUpdatedAt());
  }
}