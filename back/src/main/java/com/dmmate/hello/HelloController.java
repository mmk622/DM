package com.dmmate.hello;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {
  @GetMapping("/hello")
  public Map<String, String> hello() {
    return Map.of("message", "Hello DM!");
  }
}