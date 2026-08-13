package ru.practica2026.admin.role.mapper;

import ru.practica2026.admin.role.dto.response.PermissionResponse;
import ru.practica2026.admin.role.dto.response.RoleDetailResponse;
import ru.practica2026.admin.role.dto.response.RoleResponse;
import ru.practica2026.admin.role.entity.Role;
import ru.practica2026.admin.role.entity.RolePermission;

import java.util.List;

public final class RoleMapper {

    private RoleMapper() {
    }

    public static RoleResponse toResponse(
            Role role
    ) {
        return new RoleResponse(
                role.getBusinessKey(),
                role.getCode(),
                role.getName(),
                role.getDescription(),
                role.isSystemRole(),
                role.getStatus(),
                role.getVersion(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    public static RoleDetailResponse toDetailResponse(
            Role role,
            List<RolePermission> rolePermissions
    ) {
        List<PermissionResponse> permissions =
                rolePermissions.stream()
                        .map(RolePermission::getPermission)
                        .map(PermissionMapper::toResponse)
                        .toList();

        return new RoleDetailResponse(
                role.getBusinessKey(),
                role.getCode(),
                role.getName(),
                role.getDescription(),
                role.isSystemRole(),
                role.getStatus(),
                role.getVersion(),
                role.getCreatedAt(),
                role.getUpdatedAt(),
                permissions
        );
    }
}
