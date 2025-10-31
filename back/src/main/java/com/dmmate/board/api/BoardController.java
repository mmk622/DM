package com.dmmate.board.api;

import com.dmmate.board.domain.GenderPref;
import com.dmmate.board.domain.PartyPref;
import com.dmmate.board.dto.*;
import com.dmmate.board.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardController {

  private final PostService service;

  // 📋 게시글 목록 + 검색 + 페이징
  @GetMapping("/posts")
  public Page<PostListItem> list(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(required = false) GenderPref genderPref,
      @RequestParam(required = false) PartyPref partyPref,
      Pageable pageable) {
    return service.search(keyword, date, genderPref, partyPref, pageable);
  }

  // 📄 게시글 상세
  @GetMapping("/posts/{id}")
  public PostResponse detail(@PathVariable Long id) {
    return service.get(id);
  }

  // ✏️ 게시글 작성 (인증 필요)
  @PostMapping("/posts")
  @ResponseStatus(HttpStatus.CREATED)
  public PostResponse create(
      @AuthenticationPrincipal String email, // ✅ SpEL 제거 — 이메일 직접 주입
      @RequestBody @Valid PostCreateRequest req) {
    return service.create(email, req);
  }

  // 💬 댓글 목록 (페이징)
  @GetMapping("/posts/{id}/comments")
  public Page<CommentResponse> comments(@PathVariable Long id, Pageable pageable) {
    return service.listComments(id, pageable);
  }

  // 💬 댓글 작성 (인증 필요)
  @PostMapping("/posts/{id}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  public CommentResponse addComment(
      @PathVariable Long id,
      @AuthenticationPrincipal String email, // ✅ SpEL 제거 — 이메일 직접 주입
      @RequestBody @Valid CommentCreateRequest req) {
    return service.addComment(email, id, req);
  }

  // 🗑️ 게시글 삭제
  @DeleteMapping("/posts/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePost(
      @PathVariable Long id,
      @AuthenticationPrincipal String email) {
    service.deletePost(email, id);
  }

  // 🗑️ 댓글 삭제
  @DeleteMapping("/posts/{postId}/comments/{commentId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteComment(
      @PathVariable Long postId,
      @PathVariable Long commentId,
      @AuthenticationPrincipal String email) {
    service.deleteComment(email, postId, commentId);
  }
}