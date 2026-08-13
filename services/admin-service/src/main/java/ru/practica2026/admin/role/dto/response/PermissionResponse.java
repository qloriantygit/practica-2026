package ru.practica2026.admin.role.dto.response;

import java.util.UUID;

public record PermissionResponse(
        UUID businessKey,
        String code,
        String name,
        String description
) {
}
