package ru.practica2026.admin.savedview.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

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

import ru.practica2026.admin.savedview.dto.request.SaveViewRequest;
import ru.practica2026.admin.savedview.dto.response.SavedViewResponse;
import ru.practica2026.admin.savedview.service.SavedViewService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/saved-views")
public class SavedViewController {

    private final SavedViewService service;

    public SavedViewController(
            SavedViewService service
    ) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavedViewResponse create(
            @AuthenticationPrincipal
            Jwt jwt,

            @Valid
            @RequestBody
            SaveViewRequest request
    ) {
        return service.create(
                jwt,
                request
        );
    }

    @GetMapping
    public List<SavedViewResponse> findAll(
            @AuthenticationPrincipal
            Jwt jwt,

            @RequestParam(required = false)
            String resourceType
    ) {
        return service.findAll(
                jwt,
                resourceType
        );
    }

    @GetMapping("/{businessKey}")
    public SavedViewResponse get(
            @AuthenticationPrincipal
            Jwt jwt,

            @PathVariable
            UUID businessKey
    ) {
        return service.get(
                jwt,
                businessKey
        );
    }

    @PutMapping("/{businessKey}")
    public SavedViewResponse update(
            @AuthenticationPrincipal
            Jwt jwt,

            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            SaveViewRequest request
    ) {
        return service.update(
                jwt,
                businessKey,
                request
        );
    }

    @DeleteMapping("/{businessKey}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal
            Jwt jwt,

            @PathVariable
            UUID businessKey
    ) {
        service.delete(
                jwt,
                businessKey
        );
    }
}
