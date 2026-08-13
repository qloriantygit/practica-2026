package ru.practica2026.admin.user.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserRoleResponse(
        UUID roleBusinessKey,
        String roleCode,
        String roleName,
        Instant validFrom,
        Instant validTo,
        Instant assignedAt,
        String assignedBy,
        boolean active
) {
}
