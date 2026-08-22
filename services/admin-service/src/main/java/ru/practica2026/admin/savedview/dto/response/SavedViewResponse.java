package ru.practica2026.admin.savedview.dto.response;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record SavedViewResponse(
        UUID businessKey,
        String name,
        String resourceType,
        Map<String, Object> filters,
        String sortBy,
        String sortDirection,
        Long version,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy
) {
}
