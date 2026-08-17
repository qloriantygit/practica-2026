package ru.practica2026.admin.expert.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import ru.practica2026.admin.expert.entity.ExpertProfileStatus;

public record UpdateExpertProfileRequest(

        @NotBlank
        @Size(max = 255)
        String specialization,

        @Size(max = 4000)
        String bio,

        @NotNull
        ExpertProfileStatus status,

        @NotNull
        Boolean available
) {
}
