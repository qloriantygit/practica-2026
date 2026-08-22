package ru.practica2026.admin.savedview.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record SaveViewRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @NotBlank
        @Size(max = 100)
        String resourceType,

        @NotNull
        Map<String, Object> filters,

        @Size(max = 100)
        String sortBy,

        @Pattern(
                regexp = "(?i)ASC|DESC",
                message = "sortDirection must be ASC or DESC"
        )
        String sortDirection
) {
}
