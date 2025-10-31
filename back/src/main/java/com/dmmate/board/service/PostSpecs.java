package com.dmmate.board.service;

import com.dmmate.board.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PostSpecs {

  public static Specification<Post> keyword(String q) {
    return (root, cq, cb) -> (q == null || q.isBlank())
        ? cb.conjunction()
        : cb.like(cb.lower(root.get("title")), "%" + q.toLowerCase() + "%");
  }

  public static Specification<Post> mealDate(LocalDate d) {
    return (root, cq, cb) -> (d == null) ? cb.conjunction() : cb.equal(root.get("mealDate"), d);
  }

  public static Specification<Post> gender(GenderPref g) {
    return (root, cq, cb) -> (g == null) ? cb.conjunction() : cb.equal(root.get("genderPref"), g);
  }

  public static Specification<Post> party(PartyPref p) {
    return (root, cq, cb) -> (p == null) ? cb.conjunction() : cb.equal(root.get("partyPref"), p);
  }
}