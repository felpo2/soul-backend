package com.project.soul.user.application.dto;

import com.project.soul.user.domain.entity.User;

import java.util.UUID;

public record ProfileSummaryDTO(
        UUID id,
        String name,
        String username,
        String profilePicture,
        String bio,
        boolean privateProfile
) {
    public static ProfileSummaryDTO from(User user) {
        return new ProfileSummaryDTO(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getProfilePicture(),
                user.getBio(),
                Boolean.TRUE.equals(user.getPrivacyStatus())
        );
    }
}
