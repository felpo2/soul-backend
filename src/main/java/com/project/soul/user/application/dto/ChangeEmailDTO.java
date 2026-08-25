package com.project.soul.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailDTO(
        @NotBlank(message = "New email is required.")
        @Email(message = "New email must be valid.")
        String newEmail,

        @NotBlank(message = "Current password is required.")
        String currentPassword
) {
}
