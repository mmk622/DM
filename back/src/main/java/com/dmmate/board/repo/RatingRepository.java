package com.dmmate.board.repo;

import com.dmmate.board.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
  Optional<Rating> findByPostIdAndRaterEmail(Long postId, String raterEmail);

  @Query("select coalesce(avg(r.score), 0) from Rating r where r.postId = :postId")
  Double getAverageScore(Long postId);

  @Query("select count(r) from Rating r where r.postId = :postId")
  Long getCount(Long postId);
}