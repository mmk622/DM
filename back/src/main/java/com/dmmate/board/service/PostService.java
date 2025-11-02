package com.dmmate.board.service;

import com.dmmate.board.domain.*;
import com.dmmate.board.dto.*;
import com.dmmate.board.repo.*;
import com.dmmate.user.UserRepository;
import com.dmmate.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepo;
  private final CommentRepository commentRepo;
  private final UserRepository userRepo;

  public Page<PostListItem> search(String keyword, LocalDate date,
      GenderPref g, PartyPref p, Pageable pageable) {
    Specification<Post> spec = Specification.where(PostSpecs.keyword(keyword))
        .and(PostSpecs.mealDate(date))
        .and(PostSpecs.gender(g))
        .and(PostSpecs.party(p));
    return postRepo.findAll(spec, pageable).map(PostListItem::of);
  }

  public PostResponse get(Long id) {
    return postRepo.findById(id)
        .map(PostResponse::of)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  // 이메일 기반 authorId
  public PostResponse create(String email, PostCreateRequest req) {
    Post post = new Post();
    post.setAuthorId(email); // ← String 이메일 그대로 저장
    post.setTitle(req.title());
    post.setContent(req.content());
    post.setMealDate(req.mealDate());
    post.setGenderPref(req.genderPref());
    post.setPartyPref(req.partyPref());
    return PostResponse.of(postRepo.save(post));
  }

  public Page<CommentResponse> listComments(Long postId, Pageable pageable) {
    return commentRepo.findByPostIdOrderByCreatedAtAsc(postId, pageable)
        .map(c -> {
          String nickname = userRepo.findByEmail(c.getAuthorId())
              .map(User::getNickname)
              .orElse(null);
          return CommentResponse.of(c, nickname); // ✅ 닉네임 포함
        });
  }

  // 이메일 기반 authorId
  public CommentResponse addComment(String email, Long postId, CommentCreateRequest req) {
    postRepo.findById(postId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    Comment c = new Comment();
    c.setPostId(postId);
    c.setAuthorId(email); // ← String 이메일 그대로 저장
    c.setContent(req.content());
    Comment saved = commentRepo.save(c);

    String nickname = userRepo.findByEmail(email)
        .map(User::getNickname)
        .orElse(null);
    return CommentResponse.of(saved, nickname); // 닉네임 포함
  }

  // 🗑️ 게시글 삭제
  public void deletePost(String email, Long postId) {
    Post post = postRepo.findById(postId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    // 본인 글인지 확인
    if (!post.getAuthorId().equals(email)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인만 삭제할 수 있습니다.");
    }

    postRepo.delete(post);
  }

  // 🗑️ 댓글 삭제
  public void deleteComment(String email, Long postId, Long commentId) {
    Comment comment = commentRepo.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    // 댓글이 해당 게시글에 속하는지 검증
    if (!comment.getPostId().equals(postId)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
    }

    // 본인 댓글인지 확인
    if (!comment.getAuthorId().equals(email)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인만 삭제할 수 있습니다.");
    }

    commentRepo.delete(comment);
  }
}