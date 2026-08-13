package ru.practica2026.admin.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateUserRequest(

        @NotBlank(message = "Username is required")
        @Size(max = 100)
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        @Size(max = 255)
        String email,

        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName,

        @NotNull(message = "Organization is required")
        UUID organizationBusinessKey
) {
}
