package ru.practica2026.admin.role.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.admin.role.dto.response.PermissionResponse;
import ru.practica2026.admin.role.service.PermissionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(
            PermissionService permissionService
    ) {
        this.permissionService =
                permissionService;
    }

    @GetMapping
    public List<PermissionResponse> findAll() {
        return permissionService.findAll();
    }
}
