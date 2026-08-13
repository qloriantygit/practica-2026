package ru.practica2026.admin.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateOrganizationRequest(

        @NotBlank(message = "Organization code is required")
        @Size(max = 100, message = "Organization code must not exceed 100 characters")
        String code,

        @NotBlank(message = "Organization name is required")
        @Size(max = 255, message = "Organization name must not exceed 255 characters")
        String name,

        UUID parentBusinessKey
) {
}
