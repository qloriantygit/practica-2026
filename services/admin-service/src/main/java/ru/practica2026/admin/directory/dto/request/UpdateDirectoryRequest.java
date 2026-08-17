package ru.practica2026.admin.directory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDirectoryRequest(

        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 4000)
        String description
) {
}
