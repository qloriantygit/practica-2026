package ru.practica2026.admin.organization.dto.response;

import ru.practica2026.admin.organization.entity.OrganizationStatus;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse(
        UUID businessKey,
        String code,
        String name,
        UUID parentBusinessKey,
        OrganizationStatus status,
        Long version,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy
) {
}
