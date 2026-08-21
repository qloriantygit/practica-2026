package ru.practica2026.admin.organization.dto.response;

import java.util.UUID;

public record OrganizationRepresentativeResponse(
        UUID businessKey,
        UUID organizationBusinessKey,
        String organizationCode,
        String firstName,
        String lastName,
        String middleName,
        String position,
        String email,
        String phone,
        boolean active,
        Long version,
        String createdBy,
        String updatedBy
) {
}
