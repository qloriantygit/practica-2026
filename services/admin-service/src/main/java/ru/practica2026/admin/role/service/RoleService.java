package ru.practica2026.admin.role.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;
import ru.practica2026.admin.role.dto.request.ChangeRoleStatusRequest;
import ru.practica2026.admin.role.dto.request.CreateRoleRequest;
import ru.practica2026.admin.role.dto.request.UpdateRoleRequest;
import ru.practica2026.admin.role.dto.response.RoleDetailResponse;
import ru.practica2026.admin.role.dto.response.RoleResponse;
import ru.practica2026.admin.role.entity.Permission;
import ru.practica2026.admin.role.entity.Role;
import ru.practica2026.admin.role.entity.RolePermission;
import ru.practica2026.admin.role.entity.RolePermissionId;
import ru.practica2026.admin.role.entity.RoleStatus;
import ru.practica2026.admin.role.mapper.RoleMapper;
import ru.practica2026.admin.role.repository.PermissionRepository;
import ru.practica2026.admin.role.repository.RolePermissionRepository;
import ru.practica2026.admin.role.repository.RoleRepository;
import ru.practica2026.admin.user.repository.UserRoleRepository;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class RoleService {

    private static final String SYSTEM_ACTOR =
            "SYSTEM";

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleService(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            UserRoleRepository userRoleRepository
    ) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        return roleRepository
                .findAllByOrderByCodeAsc()
                .stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleDetailResponse get(
            UUID businessKey
    ) {
        Role role =
                findRole(businessKey);

        return buildDetailResponse(role);
    }

    @Transactional
    public RoleDetailResponse create(
            CreateRoleRequest request
    ) {
        String code =
                request.code()
                        .trim()
                        .toUpperCase(Locale.ROOT);

        if (
                roleRepository
                        .existsByCodeIgnoreCase(code)
        ) {
            throw new ConflictException(
                    "Role with code '" +
                    code +
                    "' already exists"
            );
        }

        Role role =
                new Role();

        role.setCode(code);
        role.setName(
                request.name().trim()
        );
        role.setDescription(
                normalizeNullable(
                        request.description()
                )
        );
        role.setSystemRole(false);
        role.setStatus(RoleStatus.ACTIVE);
        role.setCreatedBy(SYSTEM_ACTOR);
        role.setUpdatedBy(SYSTEM_ACTOR);

        Role saved =
                roleRepository
                        .saveAndFlush(role);

        return buildDetailResponse(saved);
    }

    @Transactional
    public RoleDetailResponse update(
            UUID businessKey,
            UpdateRoleRequest request
    ) {
        Role role =
                findRole(businessKey);

        role.setName(
                request.name().trim()
        );

        role.setDescription(
                normalizeNullable(
                        request.description()
                )
        );

        role.setUpdatedBy(SYSTEM_ACTOR);

        roleRepository.flush();

        return buildDetailResponse(role);
    }

    @Transactional
    public RoleDetailResponse changeStatus(
            UUID businessKey,
            ChangeRoleStatusRequest request
    ) {
        Role role =
                findRole(businessKey);

        if (
                role.isSystemRole()
                &&
                request.status()
                        == RoleStatus.ARCHIVED
        ) {
            throw new ConflictException(
                    "System role cannot be archived"
            );
        }

        if (
                request.status()
                        == RoleStatus.ARCHIVED
                &&
                userRoleRepository
                        .countNonExpiredAssignments(
                                role.getId(),
                                Instant.now()
                        ) > 0
        ) {
            throw new ConflictException(
                    "Role cannot be archived while it has active or future user assignments"
            );
        }

        role.setStatus(request.status());
        role.setUpdatedBy(SYSTEM_ACTOR);

        roleRepository.flush();

        return buildDetailResponse(role);
    }

    @Transactional
    public RoleDetailResponse addPermission(
            UUID roleBusinessKey,
            UUID permissionBusinessKey
    ) {
        Role role =
                findRole(roleBusinessKey);

        if (
                role.getStatus()
                        != RoleStatus.ACTIVE
        ) {
            throw new ConflictException(
                    "Permission cannot be assigned to archived role"
            );
        }

        Permission permission =
                findPermission(
                        permissionBusinessKey
                );

        RolePermission existing =
                rolePermissionRepository
                        .findByRole_IdAndPermission_Id(
                                role.getId(),
                                permission.getId()
                        )
                        .orElse(null);

        if (existing != null) {
            throw new ConflictException(
                    "Permission is already assigned to role"
            );
        }

        RolePermission rolePermission =
                new RolePermission();

        rolePermission.setId(
                new RolePermissionId(
                        role.getId(),
                        permission.getId()
                )
        );

        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermission.setGrantedAt(
                Instant.now()
        );
        rolePermission.setGrantedBy(
                SYSTEM_ACTOR
        );

        rolePermissionRepository
                .saveAndFlush(rolePermission);

        return buildDetailResponse(role);
    }

    @Transactional
    public RoleDetailResponse removePermission(
            UUID roleBusinessKey,
            UUID permissionBusinessKey
    ) {
        Role role =
                findRole(roleBusinessKey);

        Permission permission =
                findPermission(
                        permissionBusinessKey
                );

        RolePermission rolePermission =
                rolePermissionRepository
                        .findByRole_IdAndPermission_Id(
                                role.getId(),
                                permission.getId()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Role permission assignment not found"
                                        )
                        );

        rolePermissionRepository
                .delete(rolePermission);

        rolePermissionRepository.flush();

        return buildDetailResponse(role);
    }

    private RoleDetailResponse buildDetailResponse(
            Role role
    ) {
        List<RolePermission> permissions =
                rolePermissionRepository
                        .findAllByRoleIdWithPermission(
                                role.getId()
                        );

        return RoleMapper.toDetailResponse(
                role,
                permissions
        );
    }

    private Role findRole(
            UUID businessKey
    ) {
        return roleRepository
                .findByBusinessKey(businessKey)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Role not found: " +
                                        businessKey
                                )
                );
    }

    private Permission findPermission(
            UUID businessKey
    ) {
        return permissionRepository
                .findByBusinessKey(businessKey)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Permission not found: " +
                                        businessKey
                                )
                );
    }

    private String normalizeNullable(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String result = value.trim();

        return result.isBlank()
                ? null
                : result;
    }
}
