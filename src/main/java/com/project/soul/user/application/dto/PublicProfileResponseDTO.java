package com.project.soul.user.application.dto;

import com.project.soul.user.domain.entity.User;

import java.util.UUID;

public record PublicProfileResponseDTO(
        UUID id,
        String name,
        String username,
        String profilePicture,
        String bio,
        boolean privateProfile,
        long postCount,
        long followerCount,
        long followingCount
) {
    public static PublicProfileResponseDTO from(
            User user,
            long postCount,
            long followerCount,
            long followingCount) {
        return new PublicProfileResponseDTO(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getProfilePicture(),
                user.getBio(),
                Boolean.TRUE.equals(user.getPrivacyStatus()),
                postCount,
                followerCount,
                followingCount
        );
    }
}
