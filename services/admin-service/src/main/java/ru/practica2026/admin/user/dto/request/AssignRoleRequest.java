package ru.practica2026.admin.user.dto.request;

import java.time.Instant;

public record AssignRoleRequest(
        Instant validFrom,
        Instant validTo
) {
}
