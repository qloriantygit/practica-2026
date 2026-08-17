package ru.practica2026.admin.expert.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ExpertCompetencyResponse(

        UUID businessKey,

        UUID sourceItemBusinessKey,

        UUID sourceVersionBusinessKey,

        String code,

        String name,

        Integer proficiencyLevel,

        String note,

        Instant createdAt,

        String createdBy
) {
}
