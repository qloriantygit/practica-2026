package ru.practica2026.admin.expert.dto.response;

import ru.practica2026.admin.expert.entity.ExpertProfileStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ExpertProfileResponse(

        UUID businessKey,

        UUID userBusinessKey,

        String username,

        String email,

        String specialization,

        String bio,

        ExpertProfileStatus status,

        boolean available,

        List<ExpertCompetencyResponse> competencies,

        Long version,

        Instant createdAt,

        Instant updatedAt,

        String createdBy,

        String updatedBy
) {
}
