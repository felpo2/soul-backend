package com.project.soul.posts.application.dto;

import com.project.soul.posts.domain.entity.Comment;

import java.time.Instant;
import java.util.UUID;

public record CommentResponseDTO(
        UUID id,
        UUID postId,
        String content,
        Instant createdAt,
        Instant updatedAt,
        UUID userId,
        String username,
        String name,
        String profilePicture
) {
    public static CommentResponseDTO from(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getPost().getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getName(),
                comment.getUser().getProfilePicture()
        );
    }
}
