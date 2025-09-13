package com.dmmate.auth.dto;

import jakarta.validation.constraints.*;

public record OtpVerifyRequest(
    @Email @NotBlank String email,
    @NotBlank String code,
    @NotBlank String name,
    String gender) {
}