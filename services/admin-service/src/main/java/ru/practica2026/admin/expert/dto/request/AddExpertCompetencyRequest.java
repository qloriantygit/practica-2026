package ru.practica2026.admin.expert.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AddExpertCompetencyRequest(

        @NotNull
        UUID directoryItemBusinessKey,

        @Min(1)
        @Max(5)
        Integer proficiencyLevel,

        @Size(max = 4000)
        String note
) {
}
