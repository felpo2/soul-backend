package com.project.soul.user.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
        @NotBlank(message = "Refresh token is required.")
        String refreshToken
) {
}
