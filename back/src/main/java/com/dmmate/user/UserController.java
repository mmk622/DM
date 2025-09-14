package com.dmmate.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserRepository users;

  public UserController(UserRepository users) {
    this.users = users;
  }

  // JWT 필터가 Authentication의 principal에 email을 넣어둠
  @GetMapping("/me")
  public UserDto me(Authentication auth) {
    String email = (String) auth.getPrincipal();
    User u = users.findByEmail(email).orElseThrow();
    return new UserDto(u.getId(), u.getEmail(), u.getNickname());
  }

  public record UserDto(Long id, String email, String nickname) {
  }
}