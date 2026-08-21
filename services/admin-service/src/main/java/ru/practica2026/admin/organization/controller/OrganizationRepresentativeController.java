package ru.practica2026.admin.organization.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.admin.organization.dto.request.ChangeOrganizationRepresentativeStatusRequest;
import ru.practica2026.admin.organization.dto.request.CreateOrganizationRepresentativeRequest;
import ru.practica2026.admin.organization.dto.request.UpdateOrganizationRepresentativeRequest;
import ru.practica2026.admin.organization.dto.response.OrganizationRepresentativeResponse;

import ru.practica2026.admin.organization.service.OrganizationRepresentativeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/organizations/{organizationBusinessKey}/representatives"
)
public class OrganizationRepresentativeController {

    private final OrganizationRepresentativeService service;

    public OrganizationRepresentativeController(
            OrganizationRepresentativeService service
    ) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationRepresentativeResponse create(
            @PathVariable
            UUID organizationBusinessKey,

            @Valid
            @RequestBody
            CreateOrganizationRepresentativeRequest request
    ) {
        return service.create(
                organizationBusinessKey,
                request
        );
    }

    @GetMapping
    public List<OrganizationRepresentativeResponse> findAll(
            @PathVariable
            UUID organizationBusinessKey,

            @RequestParam(required = false)
            String search
    ) {
        return service.findAll(
                organizationBusinessKey,
                search
        );
    }

    @GetMapping("/{representativeBusinessKey}")
    public OrganizationRepresentativeResponse get(
            @PathVariable
            UUID organizationBusinessKey,

            @PathVariable
            UUID representativeBusinessKey
    ) {
        return service.get(
                organizationBusinessKey,
                representativeBusinessKey
        );
    }

    @PutMapping("/{representativeBusinessKey}")
    public OrganizationRepresentativeResponse update(
            @PathVariable
            UUID organizationBusinessKey,

            @PathVariable
            UUID representativeBusinessKey,

            @Valid
            @RequestBody
            UpdateOrganizationRepresentativeRequest request
    ) {
        return service.update(
                organizationBusinessKey,
                representativeBusinessKey,
                request
        );
    }

    @PatchMapping("/{representativeBusinessKey}/status")
    public OrganizationRepresentativeResponse changeStatus(
            @PathVariable
            UUID organizationBusinessKey,

            @PathVariable
            UUID representativeBusinessKey,

            @RequestBody
            ChangeOrganizationRepresentativeStatusRequest request
    ) {
        return service.changeStatus(
                organizationBusinessKey,
                representativeBusinessKey,
                request
        );
    }
}
