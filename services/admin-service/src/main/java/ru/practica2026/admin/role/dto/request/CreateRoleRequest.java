package ru.practica2026.admin.role.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(

        @NotBlank(message = "Role code is required")
        @Size(max = 100)
        String code,

        @NotBlank(message = "Role name is required")
        @Size(max = 255)
        String name,

        @Size(max = 2000)
        String description
) {
}
