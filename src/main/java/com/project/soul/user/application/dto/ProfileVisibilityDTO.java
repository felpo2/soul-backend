package com.project.soul.user.application.dto;

import jakarta.validation.constraints.NotNull;

public record ProfileVisibilityDTO(
        @NotNull(message = "Profile visibility is required.")
        Boolean privateProfile
) {
}
