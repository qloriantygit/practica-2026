package ru.practica2026.admin.role.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.admin.role.dto.request.ChangeRoleStatusRequest;
import ru.practica2026.admin.role.dto.request.CreateRoleRequest;
import ru.practica2026.admin.role.dto.request.UpdateRoleRequest;
import ru.practica2026.admin.role.dto.response.RoleDetailResponse;
import ru.practica2026.admin.role.dto.response.RoleResponse;
import ru.practica2026.admin.role.service.RoleService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(
            RoleService roleService
    ) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleResponse> findAll() {
        return roleService.findAll();
    }

    @GetMapping("/{businessKey}")
    public RoleDetailResponse get(
            @PathVariable
            UUID businessKey
    ) {
        return roleService.get(businessKey);
    }

    @PostMapping
    public ResponseEntity<RoleDetailResponse> create(
            @Valid
            @RequestBody
            CreateRoleRequest request
    ) {
        RoleDetailResponse response =
                roleService.create(request);

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/roles/" +
                                response.businessKey()
                        )
                )
                .body(response);
    }

    @PutMapping("/{businessKey}")
    public RoleDetailResponse update(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            UpdateRoleRequest request
    ) {
        return roleService.update(
                businessKey,
                request
        );
    }

    @PatchMapping("/{businessKey}/status")
    public RoleDetailResponse changeStatus(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            ChangeRoleStatusRequest request
    ) {
        return roleService.changeStatus(
                businessKey,
                request
        );
    }

    @PostMapping(
            "/{roleBusinessKey}/permissions/{permissionBusinessKey}"
    )
    public RoleDetailResponse addPermission(

            @PathVariable
            UUID roleBusinessKey,

            @PathVariable
            UUID permissionBusinessKey
    ) {
        return roleService.addPermission(
                roleBusinessKey,
                permissionBusinessKey
        );
    }

    @DeleteMapping(
            "/{roleBusinessKey}/permissions/{permissionBusinessKey}"
    )
    public RoleDetailResponse removePermission(

            @PathVariable
            UUID roleBusinessKey,

            @PathVariable
            UUID permissionBusinessKey
    ) {
        return roleService.removePermission(
                roleBusinessKey,
                permissionBusinessKey
        );
    }
}
