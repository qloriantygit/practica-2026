package ru.practica2026.admin.document.dto.response;

import java.time.Instant;
import java.util.UUID;

public record MandatoryDocumentRuleResponse(

        UUID businessKey,

        String contextCode,

        UUID documentTypeBusinessKey,

        String documentTypeCode,

        String documentTypeName,

        boolean mandatory,

        boolean active,

        Long version,

        Instant createdAt,

        Instant updatedAt,

        String createdBy,

        String updatedBy
) {
}
