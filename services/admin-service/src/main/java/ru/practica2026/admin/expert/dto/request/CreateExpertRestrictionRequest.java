package ru.practica2026.admin.expert.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateExpertRestrictionRequest(

        @NotBlank
        @Size(max = 100)
        String code,

        @NotBlank
        @Size(max = 1000)
        String description,

        LocalDate validFrom,

        LocalDate validTo
) {
}
