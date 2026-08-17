package ru.practica2026.admin.document.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SaveMandatoryDocumentRuleRequest(

        @NotBlank
        @Size(max = 100)
        String contextCode,

        @NotNull
        UUID documentTypeBusinessKey,

        @NotNull
        Boolean mandatory,

        Boolean active
) {
}
