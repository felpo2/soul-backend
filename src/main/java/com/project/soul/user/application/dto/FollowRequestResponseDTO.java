package com.project.soul.user.application.dto;

import com.project.soul.user.domain.entity.Follow;

import java.time.Instant;
import java.util.UUID;

public record FollowRequestResponseDTO(
        UUID id,
        ProfileSummaryDTO follower,
        Instant createdAt
) {
    public static FollowRequestResponseDTO from(Follow follow) {
        return new FollowRequestResponseDTO(
                follow.getId(),
                ProfileSummaryDTO.from(follow.getFollower()),
                follow.getCreatedAt()
        );
    }
}
