package ru.practica2026.admin.organization.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrganizationRepresentativeRequest(

        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotBlank
        @Size(max = 100)
        String lastName,

        @Size(max = 100)
        String middleName,

        @NotBlank
        @Size(max = 200)
        String position,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @Size(max = 50)
        String phone
) {
}
