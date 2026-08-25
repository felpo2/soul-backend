package com.project.soul.user.application.dto;

public record UpdateUserRequestDTO(
        String name,
        String bio,
        String profilePicture
) {
}
