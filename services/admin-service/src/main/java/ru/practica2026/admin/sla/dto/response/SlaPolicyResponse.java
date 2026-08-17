package ru.practica2026.admin.sla.dto.response;

import java.time.Instant;
import java.util.UUID;

public record SlaPolicyResponse(

        UUID businessKey,

        String code,

        String name,

        String description,

        Integer responseMinutes,

        Integer resolutionMinutes,

        UUID calendarBusinessKey,

        String calendarCode,

        boolean active,

        Long version,

        Instant createdAt,

        Instant updatedAt,

        String createdBy,

        String updatedBy
) {
}
