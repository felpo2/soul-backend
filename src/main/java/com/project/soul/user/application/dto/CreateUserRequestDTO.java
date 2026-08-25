package com.project.soul.user.application.dto;

import java.util.Date;

public record CreateUserRequestDTO(
        String name,
        String username,
        String email,
        String password,
        Date dateOfBirth,
        String profilePicture,
        String bio
) {
}
