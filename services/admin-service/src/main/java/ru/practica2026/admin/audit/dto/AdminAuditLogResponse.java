package ru.practica2026.admin.audit.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminAuditLogResponse(

        UUID businessKey,

        String correlationId,

        String actor,

        String httpMethod,

        String requestPath,

        String action,

        String entityType,

        String entityKey,

        String beforeState,

        String afterState,

        boolean success,

        String errorMessage,

        Instant createdAt
) {
}
