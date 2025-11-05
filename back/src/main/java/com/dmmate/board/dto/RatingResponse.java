package com.dmmate.board.dto;

public record RatingResponse(
    Long postId,
    Integer myScore // 0~10, null이면 아직 안 남김
) {
}