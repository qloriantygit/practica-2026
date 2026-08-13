package ru.practica2026.admin.user.mapper;

import ru.practica2026.admin.user.dto.response.UserDetailResponse;
import ru.practica2026.admin.user.dto.response.UserResponse;
import ru.practica2026.admin.user.dto.response.UserRoleResponse;
import ru.practica2026.admin.user.entity.UserAccount;
import ru.practica2026.admin.user.entity.UserRole;
import ru.practica2026.admin.role.entity.RoleStatus;

import java.time.Instant;
import java.util.List;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(
            UserAccount user
    ) {
        return new UserResponse(
                user.getBusinessKey(),
                user.getExternalId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),

                user.getOrganization() == null
                        ? null
                        : user.getOrganization()
                                .getBusinessKey(),

                user.getOrganization() == null
                        ? null
                        : user.getOrganization()
                                .getCode(),

                user.getStatus(),
                user.getVersion(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public static UserDetailResponse toDetailResponse(
            UserAccount user,
            List<UserRole> assignments
    ) {
        Instant now = Instant.now();

        List<UserRoleResponse> roles =
                assignments.stream()
                        .map(
                                assignment ->
                                        toRoleResponse(
                                                assignment,
                                                now
                                        )
                        )
                        .toList();

        return new UserDetailResponse(
                user.getBusinessKey(),
                user.getExternalId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),

                user.getOrganization() == null
                        ? null
                        : user.getOrganization()
                                .getBusinessKey(),

                user.getOrganization() == null
                        ? null
                        : user.getOrganization()
                                .getCode(),

                user.getStatus(),
                user.getVersion(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                roles
        );
    }

    private static UserRoleResponse toRoleResponse(
            UserRole assignment,
            Instant now
    ) {
        boolean active =
                !now.isBefore(
                        assignment.getValidFrom()
                )
                &&
                (
                    assignment.getValidTo() == null
                    ||
                    now.isBefore(
                            assignment.getValidTo()
                    )
                )
                &&
                assignment.getRole().getStatus()
                        == RoleStatus.ACTIVE;

        return new UserRoleResponse(
                assignment.getRole()
                        .getBusinessKey(),

                assignment.getRole()
                        .getCode(),

                assignment.getRole()
                        .getName(),

                assignment.getValidFrom(),
                assignment.getValidTo(),
                assignment.getAssignedAt(),
                assignment.getAssignedBy(),
                active
        );
    }
}
