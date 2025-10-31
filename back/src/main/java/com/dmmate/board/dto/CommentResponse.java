package com.dmmate.board.dto;

import com.dmmate.board.domain.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
    Long id,
    Long postId,
    String authorId,
    String content,
    LocalDateTime createdAt) {
  public static CommentResponse of(Comment c) {
    return new CommentResponse(
        c.getId(), c.getPostId(), c.getAuthorId(), c.getContent(), c.getCreatedAt());
  }
}