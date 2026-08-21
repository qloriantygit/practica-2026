package ru.practica2026.admin.user.dto.response;

import java.util.UUID;

public record IdpSyncResponse(
        UUID businessKey,
        String username,
        String externalId,
        String email,
        String firstName,
        String lastName,
        boolean synchronizedSuccessfully
) {
}
