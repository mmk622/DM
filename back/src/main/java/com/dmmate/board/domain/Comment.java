package com.dmmate.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.*;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long postId;
  @Column(nullable = false, length = 255)
  private String authorId;
  @Lob
  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}