package ru.practica2026.admin.role.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.role.dto.response.PermissionResponse;
import ru.practica2026.admin.role.mapper.PermissionMapper;
import ru.practica2026.admin.role.repository.PermissionRepository;

import java.util.List;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(
            PermissionRepository permissionRepository
    ) {
        this.permissionRepository =
                permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> findAll() {
        return permissionRepository
                .findAllByOrderByCodeAsc()
                .stream()
                .map(PermissionMapper::toResponse)
                .toList();
    }
}
