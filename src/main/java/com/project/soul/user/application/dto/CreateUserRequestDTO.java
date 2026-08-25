package com.project.soul.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record CreateUserRequestDTO(
        @NotBlank(message = "Name is required.")
        @Size(max = 120, message = "Name must have at most 120 characters.")
        String name,

        @NotBlank(message = "Username is required.")
        @Size(min = 3, max = 50, message = "Username must have between 3 and 50 characters.")
        String username,

        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be valid.")
        String email,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 72, message = "Password must have between 8 and 72 characters.")
        String password,

        @NotNull(message = "Date of birth is required.")
        @Past(message = "Date of birth must be in the past.")
        Date dateOfBirth,

        @Size(max = 2048, message = "Profile picture URL must have at most 2048 characters.")
        String profilePicture,

        @Size(max = 500, message = "Bio must have at most 500 characters.")
        String bio
) {
}
