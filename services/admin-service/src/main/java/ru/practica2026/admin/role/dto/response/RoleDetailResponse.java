package ru.practica2026.admin.role.dto.response;

import ru.practica2026.admin.role.entity.RoleStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RoleDetailResponse(
        UUID businessKey,
        String code,
        String name,
        String description,
        boolean systemRole,
        RoleStatus status,
        Long version,
        Instant createdAt,
        Instant updatedAt,
        List<PermissionResponse> permissions
) {
}
