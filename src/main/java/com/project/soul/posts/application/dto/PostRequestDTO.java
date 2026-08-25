package com.project.soul.posts.application.dto;

import jakarta.validation.constraints.Size;

public record PostRequestDTO(
        @Size(max = 5000, message = "Content must have at most 5000 characters.")
        String content,

        @Size(max = 2048, message = "Image URL must have at most 2048 characters.")
        String imageUrl
) {
}
