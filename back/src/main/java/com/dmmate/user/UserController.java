package com.dmmate.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserRepository repo;

  public UserController(UserRepository repo) {
    this.repo = repo;
  }

  @GetMapping("/me")
  public UserDto me(@AuthenticationPrincipal User principal) {
    return repo.findByEmail(principal.getUsername())
        .map(u -> new UserDto(u.getId(), u.getEmail(), u.getName()))
        .orElseThrow();
  }

  public record UserDto(Long id, String email, String name) {
  }
}
