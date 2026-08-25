package com.project.soul.user.application.dto;

public record LoginResponseDTO(
        String token,
        String refreshToken,
        UserResponseDTO user
) {

}
