package ru.practica2026.admin.expert.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ExpertRestrictionResponse(
        UUID businessKey,
        UUID expertBusinessKey,
        String code,
        String description,
        LocalDate validFrom,
        LocalDate validTo,
        boolean active,
        Long version,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy
) {
}
