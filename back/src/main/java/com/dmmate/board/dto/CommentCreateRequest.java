package com.dmmate.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(
                @NotBlank String content, @NotNull Boolean secret) {
}
