package ru.practica2026.admin.role.mapper;

import ru.practica2026.admin.role.dto.response.PermissionResponse;
import ru.practica2026.admin.role.entity.Permission;

public final class PermissionMapper {

    private PermissionMapper() {
    }

    public static PermissionResponse toResponse(
            Permission permission
    ) {
        return new PermissionResponse(
                permission.getBusinessKey(),
                permission.getCode(),
                permission.getName(),
                permission.getDescription()
        );
    }
}
