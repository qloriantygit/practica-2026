package ru.practica2026.admin.user.dto.request;

import jakarta.validation.constraints.NotNull;

import ru.practica2026.admin.user.entity.UserStatus;

public record ChangeUserStatusRequest(

        @NotNull(message = "User status is required")
        UserStatus status
) {
}
