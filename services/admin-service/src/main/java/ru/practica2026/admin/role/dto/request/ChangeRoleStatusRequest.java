package ru.practica2026.admin.role.dto.request;

import jakarta.validation.constraints.NotNull;

import ru.practica2026.admin.role.entity.RoleStatus;

public record ChangeRoleStatusRequest(

        @NotNull(message = "Role status is required")
        RoleStatus status
) {
}
