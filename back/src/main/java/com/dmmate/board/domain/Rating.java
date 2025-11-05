package com.dmmate.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "post_ratings", uniqueConstraints = @UniqueConstraint(name = "uq_rating", columnNames = { "post_id",
    "rater_email" }))
@Getter
@Setter
public class Rating {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "post_id", nullable = false)
  private Long postId;

  @Column(name = "rater_email", nullable = false, length = 255)
  private String raterEmail;

  // 0~10 (별5개, 반개=1점)
  @Column(nullable = false)
  private Integer score;
}