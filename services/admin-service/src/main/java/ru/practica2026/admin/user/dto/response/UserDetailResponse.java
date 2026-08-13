package ru.practica2026.admin.user.dto.response;

import ru.practica2026.admin.user.entity.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserDetailResponse(
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
        Instant updatedAt,
        List<UserRoleResponse> roles
) {
}
