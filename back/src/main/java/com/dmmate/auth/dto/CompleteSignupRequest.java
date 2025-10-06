package com.dmmate.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record CompleteSignupRequest(
    @NotBlank String signupToken,
    @NotBlank String name,
    @NotBlank String nickname,
    @NotBlank String password
) {}