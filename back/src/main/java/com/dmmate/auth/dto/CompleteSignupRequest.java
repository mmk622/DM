package com.dmmate.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompleteSignupRequest(
        @NotBlank @Email String email,
        @NotBlank String name,
        @NotBlank String nickname,
        @NotBlank String password
) {}