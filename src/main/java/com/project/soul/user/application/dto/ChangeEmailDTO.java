package com.project.soul.user.application.dto;

public record ChangeEmailDTO(
        String newEmail,
        String currentPassword
) {
}
