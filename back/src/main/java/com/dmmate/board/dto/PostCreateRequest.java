package com.dmmate.board.dto;

import com.dmmate.board.domain.GenderPref;
import com.dmmate.board.domain.PartyPref;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PostCreateRequest(
    @NotBlank String title,
    @NotBlank String content,
    @NotNull LocalDate mealDate,
    @NotNull GenderPref genderPref,
    @NotNull PartyPref partyPref) {
}