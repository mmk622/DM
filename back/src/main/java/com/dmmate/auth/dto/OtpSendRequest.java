package com.dmmate.auth.dto;

import jakarta.validation.constraints.*;

public record OtpSendRequest(
    @Email @NotBlank String email) {
}