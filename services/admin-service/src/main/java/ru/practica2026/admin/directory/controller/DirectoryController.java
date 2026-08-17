package ru.practica2026.admin.directory.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.admin.directory.dto.request.CreateDirectoryRequest;
import ru.practica2026.admin.directory.dto.request.UpdateDirectoryRequest;
import ru.practica2026.admin.directory.dto.response.DirectoryDetailResponse;
import ru.practica2026.admin.directory.dto.response.DirectoryPageResponse;
import ru.practica2026.admin.directory.dto.response.DirectoryVersionResponse;
import ru.practica2026.admin.directory.entity.DirectoryVersionStatus;
import ru.practica2026.admin.directory.service.DirectoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/directories")
public class DirectoryController {

    private final DirectoryService directoryService;

    public DirectoryController(
            DirectoryService directoryService
    ) {
        this.directoryService = directoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectoryDetailResponse create(
            @Valid
            @RequestBody
            CreateDirectoryRequest request
    ) {
        return directoryService.create(request);
    }

    @GetMapping
    public DirectoryPageResponse findAll(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            DirectoryVersionStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(defaultValue = "code")
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            String direction
    ) {
        return directoryService.findAll(
                search,
                status,
                page,
                size,
                sortBy,
                direction
        );
    }

    @GetMapping("/{businessKey}")
    public DirectoryDetailResponse findByBusinessKey(
            @PathVariable
            UUID businessKey
    ) {
        return directoryService.findByBusinessKey(
                businessKey
        );
    }

    @PutMapping("/{businessKey}")
    public DirectoryDetailResponse update(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            UpdateDirectoryRequest request
    ) {
        return directoryService.update(
                businessKey,
                request
        );
    }

    @GetMapping("/{businessKey}/versions")
    public List<DirectoryVersionResponse> findVersions(
            @PathVariable
            UUID businessKey
    ) {
        return directoryService.findVersions(
                businessKey
        );
    }
}
