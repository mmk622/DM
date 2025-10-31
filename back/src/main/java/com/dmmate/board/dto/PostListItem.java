package com.dmmate.board.dto;

import com.dmmate.board.domain.Post;
import com.dmmate.board.domain.GenderPref;
import com.dmmate.board.domain.PartyPref;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PostListItem(
    Long id,
    String authorId,
    String title,
    LocalDate mealDate,
    GenderPref genderPref,
    PartyPref partyPref,
    LocalDateTime createdAt) {
  public static PostListItem of(Post p) {
    return new PostListItem(
        p.getId(), p.getAuthorId(), p.getTitle(),
        p.getMealDate(), p.getGenderPref(), p.getPartyPref(),
        p.getCreatedAt());
  }
}