package ru.practica2026.admin.sla.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateSlaPolicyRequest(

        @NotBlank
        @Size(max = 100)
        String code,

        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 4000)
        String description,

        @NotNull
        @Min(1)
        Integer responseMinutes,

        @NotNull
        @Min(1)
        Integer resolutionMinutes,

        @NotNull
        UUID calendarBusinessKey,

        Boolean active
) {
}
