package ru.practica2026.admin.template.dto.response;

import ru.practica2026.admin.template.entity.TemplateChannel;
import ru.practica2026.admin.template.entity.TemplateType;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record TemplateResponse(

        UUID businessKey,

        String code,

        String name,

        String description,

        TemplateType templateType,

        TemplateChannel channel,

        String subject,

        String body,

        Set<String> variables,

        boolean active,

        Long version,

        Instant createdAt,

        Instant updatedAt,

        String createdBy,

        String updatedBy
) {
}
