package com.project.soul.user.application.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record LoginRequestDTO(
        String email,

        String password
) {
}