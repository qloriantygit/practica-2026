package ru.practica2026.admin.directory.dto.response;

import ru.practica2026.admin.directory.entity.DirectoryVersionStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DirectoryVersionResponse(

        UUID businessKey,

        Integer versionNumber,

        DirectoryVersionStatus status,

        LocalDate validFrom,

        LocalDate validTo,

        long itemCount,

        Long version,

        Instant createdAt,

        Instant updatedAt,

        String createdBy,

        String updatedBy
) {
}
