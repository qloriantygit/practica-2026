package ru.practica2026.admin.expert.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateExpertProfileRequest(

        @NotNull
        UUID userBusinessKey,

        @NotBlank
        @Size(max = 255)
        String specialization,

        @Size(max = 4000)
        String bio,

        Boolean available
) {
}
