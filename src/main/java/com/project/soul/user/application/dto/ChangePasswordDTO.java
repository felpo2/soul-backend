package com.project.soul.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(
        @NotBlank(message = "Current password is required.")
        String currentPassword,

        @NotBlank(message = "New password is required.")
        @Size(min = 8, max = 72, message = "New password must have between 8 and 72 characters.")
        String newPassword
) {
}
