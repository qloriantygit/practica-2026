package ru.practica2026.admin.directory.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DirectoryDetailResponse(

        UUID businessKey,

        String code,

        String name,

        String description,

        Long version,

        Instant createdAt,

        Instant updatedAt,

        String createdBy,

        String updatedBy,

        List<DirectoryVersionResponse> versions
) {
}
