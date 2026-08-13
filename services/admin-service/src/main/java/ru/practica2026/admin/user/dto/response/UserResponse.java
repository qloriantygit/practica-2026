package ru.practica2026.admin.user.dto.response;

import ru.practica2026.admin.user.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID businessKey,
        String externalId,
        String username,
        String email,
        String firstName,
        String lastName,
        UUID organizationBusinessKey,
        String organizationCode,
        UserStatus status,
        Long version,
        Instant createdAt,
        Instant updatedAt
) {
}
