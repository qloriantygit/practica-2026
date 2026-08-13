package ru.practica2026.admin.organization.dto.request;

import jakarta.validation.constraints.NotNull;

import ru.practica2026.admin.organization.entity.OrganizationStatus;

public record ChangeOrganizationStatusRequest(

        @NotNull(message = "Organization status is required")
        OrganizationStatus status
) {
}
