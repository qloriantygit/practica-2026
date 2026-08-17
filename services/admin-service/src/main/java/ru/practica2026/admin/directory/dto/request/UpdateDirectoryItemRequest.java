package ru.practica2026.admin.directory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record UpdateDirectoryItemRequest(

        @NotBlank
        @Size(max = 100)
        String code,

        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 4000)
        String description,

        Boolean enabled,

        @Min(0)
        Integer sortOrder,

        Map<String, Object> attributes
) {
}
