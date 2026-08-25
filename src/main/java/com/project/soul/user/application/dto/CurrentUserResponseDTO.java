package com.project.soul.user.application.dto;

import com.project.soul.user.domain.entity.User;

import java.util.Date;
import java.util.UUID;

public record CurrentUserResponseDTO(
        UUID id,
        String name,
        String username,
        String email,
        Date dateOfBirth,
        String profilePicture,
        String bio,
        Date createdAt,
        Boolean accountStatus,
        Boolean privacyStatus,
        Boolean metricsStatus
) {
    public static CurrentUserResponseDTO from(User user) {
        return new CurrentUserResponseDTO(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getProfilePicture(),
                user.getBio(),
                user.getCreatedAt(),
                user.getAccountStatus(),
                user.getPrivacyStatus(),
                user.getMetricsStatus()
        );
    }
}
