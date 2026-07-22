package com.project.soul.user.application.dto;

public record ChangePasswordDTO(
        String currentPassword,
        String newPassword
) {
}
