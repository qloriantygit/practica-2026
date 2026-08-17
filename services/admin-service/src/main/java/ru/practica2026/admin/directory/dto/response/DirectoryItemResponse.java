package ru.practica2026.admin.directory.dto.response;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record DirectoryItemResponse(

        UUID businessKey,

        UUID directoryVersionBusinessKey,

        String code,

        String name,

        String description,

        boolean enabled,

        Integer sortOrder,

        Map<String, Object> attributes,

        Long version,

        Instant createdAt,

        Instant updatedAt,

        String createdBy,

        String updatedBy
) {
}
