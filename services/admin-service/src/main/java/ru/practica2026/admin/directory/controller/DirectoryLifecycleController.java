package ru.practica2026.admin.directory.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.admin.directory.dto.request.CreateDirectoryVersionRequest;
import ru.practica2026.admin.directory.dto.response.DirectoryVersionResponse;
import ru.practica2026.admin.directory.service.DirectoryLifecycleService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class DirectoryLifecycleController {

    private final DirectoryLifecycleService lifecycleService;

    public DirectoryLifecycleController(
            DirectoryLifecycleService lifecycleService
    ) {
        this.lifecycleService = lifecycleService;
    }

    @PostMapping(
            "/directory-versions/{versionBusinessKey}/submit"
    )
    public DirectoryVersionResponse submit(
            @PathVariable
            UUID versionBusinessKey
    ) {
        return lifecycleService.submit(
                versionBusinessKey
        );
    }

    @PostMapping(
            "/directories/{directoryBusinessKey}/versions"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public DirectoryVersionResponse createNextVersion(
            @PathVariable
            UUID directoryBusinessKey,

            @Valid
            @RequestBody
            CreateDirectoryVersionRequest request
    ) {
        return lifecycleService.createNextVersion(
                directoryBusinessKey,
                request
        );
    }
}
