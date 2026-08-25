package com.project.soul.user.application.dto;

import com.project.soul.user.domain.entity.User;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String username,
        String profilePicture,
        String bio,
        Boolean accountStatus,
        Boolean privacyStatus,
        Boolean metricsStatus
) {
    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getProfilePicture(),
                user.getBio(),
                user.getAccountStatus(),
                user.getPrivacyStatus(),
                user.getMetricsStatus()
        );
    }
}
