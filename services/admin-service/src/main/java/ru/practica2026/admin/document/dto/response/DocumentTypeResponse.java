package ru.practica2026.admin.document.dto.response;

import java.time.Instant;
import java.util.UUID;

public record DocumentTypeResponse(

        UUID businessKey,

        String code,

        String name,

        String description,

        boolean active,

        Long version,

        Instant createdAt,

        Instant updatedAt,

        String createdBy,

        String updatedBy
) {
}
