package com.project.soul.posts.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PostResponseDTO {

    private UUID id;
    private String content;
    private String imageUrl;
    private Date createdAt;

    private UUID userId;
    private String username;
    private String name;
    private String profilePicture;
}