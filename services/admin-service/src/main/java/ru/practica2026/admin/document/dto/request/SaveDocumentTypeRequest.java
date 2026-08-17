package ru.practica2026.admin.document.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveDocumentTypeRequest(

        @NotBlank
        @Size(max = 100)
        String code,

        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 4000)
        String description,

        Boolean active
) {
}
