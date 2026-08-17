package ru.practica2026.admin.directory.dto.response;

import ru.practica2026.admin.directory.entity.DirectoryVersionStatus;

import java.time.Instant;
import java.util.UUID;

public record DirectorySummaryResponse(

        UUID businessKey,

        String code,

        String name,

        String description,

        Integer latestVersionNumber,

        DirectoryVersionStatus latestStatus,

        Long version,

        Instant createdAt,

        Instant updatedAt,

        String createdBy,

        String updatedBy
) {
}
