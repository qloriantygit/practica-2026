package ru.practica2026.admin.approval.dto.response;

import ru.practica2026.admin.approval.entity.ApprovalResourceType;
import ru.practica2026.admin.approval.entity.ApprovalStatus;

import java.time.Instant;
import java.util.UUID;

public record ApprovalResponse(

        UUID businessKey,

        ApprovalResourceType resourceType,

        UUID resourceKey,

        ApprovalStatus status,

        String requestedBy,

        Instant requestedAt,

        String decidedBy,

        Instant decidedAt,

        String decisionComment,

        Long version,

        Instant createdAt,

        Instant updatedAt
) {
}
