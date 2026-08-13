package ru.practica2026.admin.organization.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.admin.common.response.PageResponse;
import ru.practica2026.admin.organization.dto.request.ChangeOrganizationStatusRequest;
import ru.practica2026.admin.organization.dto.request.CreateOrganizationRequest;
import ru.practica2026.admin.organization.dto.request.UpdateOrganizationRequest;
import ru.practica2026.admin.organization.dto.response.OrganizationResponse;
import ru.practica2026.admin.organization.entity.OrganizationStatus;
import ru.practica2026.admin.organization.service.OrganizationService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@Validated
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(
            OrganizationService organizationService
    ) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> create(
            @Valid
            @RequestBody
            CreateOrganizationRequest request
    ) {
        OrganizationResponse response =
                organizationService.create(request);

        URI location = URI.create(
                "/api/v1/organizations/" +
                response.businessKey()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping("/{businessKey}")
    public OrganizationResponse get(
            @PathVariable
            UUID businessKey
    ) {
        return organizationService.get(businessKey);
    }

    @GetMapping
    public PageResponse<OrganizationResponse> findAll(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            OrganizationStatus status,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size,

            @RequestParam(defaultValue = "name")
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            Sort.Direction direction
    ) {
        return organizationService.findAll(
                search,
                status,
                page,
                size,
                sortBy,
                direction
        );
    }

    @PutMapping("/{businessKey}")
    public OrganizationResponse update(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            UpdateOrganizationRequest request
    ) {
        return organizationService.update(
                businessKey,
                request
        );
    }

    @PatchMapping("/{businessKey}/status")
    public OrganizationResponse changeStatus(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            ChangeOrganizationStatusRequest request
    ) {
        return organizationService.changeStatus(
                businessKey,
                request
        );
    }
}
