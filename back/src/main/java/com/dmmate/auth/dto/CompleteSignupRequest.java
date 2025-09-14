package com.dmmate.auth.dto;

import jakarta.validation.constraints.*;

public record CompleteSignupRequest(
    @NotBlank @Pattern(regexp = "^[a-zA-Z0-9_가-힣]{2,20}$") String nickname,
    @NotBlank @Size(min = 8, max = 72) String password) {
}