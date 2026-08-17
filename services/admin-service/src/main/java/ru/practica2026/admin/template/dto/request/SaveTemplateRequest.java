package ru.practica2026.admin.template.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import ru.practica2026.admin.template.entity.TemplateChannel;
import ru.practica2026.admin.template.entity.TemplateType;

import java.util.Set;

public record SaveTemplateRequest(

        @NotBlank
        @Size(max = 100)
        String code,

        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 4000)
        String description,

        @NotNull
        TemplateType templateType,

        TemplateChannel channel,

        @Size(max = 500)
        String subject,

        @NotBlank
        String body,

        Set<String> variables,

        Boolean active
) {
}
