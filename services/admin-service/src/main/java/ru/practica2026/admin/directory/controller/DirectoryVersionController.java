package ru.practica2026.admin.directory.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.admin.directory.dto.request.CreateDirectoryItemRequest;
import ru.practica2026.admin.directory.dto.request.UpdateDirectoryItemRequest;
import ru.practica2026.admin.directory.dto.response.DirectoryItemPageResponse;
import ru.practica2026.admin.directory.dto.response.DirectoryItemResponse;
import ru.practica2026.admin.directory.dto.response.DirectoryVersionResponse;
import ru.practica2026.admin.directory.service.DirectoryService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/directory-versions")
public class DirectoryVersionController {

    private final DirectoryService directoryService;

    public DirectoryVersionController(
            DirectoryService directoryService
    ) {
        this.directoryService = directoryService;
    }

    @GetMapping("/{versionBusinessKey}")
    public DirectoryVersionResponse findVersion(
            @PathVariable
            UUID versionBusinessKey
    ) {
        return directoryService.findVersion(
                versionBusinessKey
        );
    }

    @GetMapping("/{versionBusinessKey}/items")
    public DirectoryItemPageResponse findItems(
            @PathVariable
            UUID versionBusinessKey,

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            Boolean enabled,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(defaultValue = "sortOrder")
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            String direction
    ) {
        return directoryService.findItems(
                versionBusinessKey,
                search,
                enabled,
                page,
                size,
                sortBy,
                direction
        );
    }

    @PostMapping("/{versionBusinessKey}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public DirectoryItemResponse createItem(
            @PathVariable
            UUID versionBusinessKey,

            @Valid
            @RequestBody
            CreateDirectoryItemRequest request
    ) {
        return directoryService.createItem(
                versionBusinessKey,
                request
        );
    }

    @PutMapping(
            "/{versionBusinessKey}/items/{itemBusinessKey}"
    )
    public DirectoryItemResponse updateItem(
            @PathVariable
            UUID versionBusinessKey,

            @PathVariable
            UUID itemBusinessKey,

            @Valid
            @RequestBody
            UpdateDirectoryItemRequest request
    ) {
        return directoryService.updateItem(
                versionBusinessKey,
                itemBusinessKey,
                request
        );
    }

    @DeleteMapping(
            "/{versionBusinessKey}/items/{itemBusinessKey}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(
            @PathVariable
            UUID versionBusinessKey,

            @PathVariable
            UUID itemBusinessKey
    ) {
        directoryService.deleteItem(
                versionBusinessKey,
                itemBusinessKey
        );
    }
}
