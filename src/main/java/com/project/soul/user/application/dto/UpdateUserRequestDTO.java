package com.project.soul.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDTO(
        @NotBlank(message = "Name is required.")
        @Size(max = 120, message = "Name must have at most 120 characters.")
        String name,

        @Size(max = 500, message = "Bio must have at most 500 characters.")
        String bio,

        @Size(max = 2048, message = "Profile picture URL must have at most 2048 characters.")
        String profilePicture
) {
}
