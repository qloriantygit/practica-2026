package ru.practica2026.admin.expert.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.admin.expert.dto.request.ChangeExpertRestrictionStatusRequest;
import ru.practica2026.admin.expert.dto.request.CreateExpertRestrictionRequest;
import ru.practica2026.admin.expert.dto.request.UpdateExpertRestrictionRequest;
import ru.practica2026.admin.expert.dto.response.ExpertRestrictionResponse;
import ru.practica2026.admin.expert.service.ExpertRestrictionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/experts/{expertBusinessKey}/restrictions"
)
public class ExpertRestrictionController {

    private final ExpertRestrictionService service;

    public ExpertRestrictionController(
            ExpertRestrictionService service
    ) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpertRestrictionResponse create(
            @PathVariable
            UUID expertBusinessKey,

            @Valid
            @RequestBody
            CreateExpertRestrictionRequest request
    ) {
        return service.create(
                expertBusinessKey,
                request
        );
    }

    @GetMapping
    public List<ExpertRestrictionResponse> findAll(
            @PathVariable
            UUID expertBusinessKey
    ) {
        return service.findAll(
                expertBusinessKey
        );
    }

    @GetMapping("/{restrictionBusinessKey}")
    public ExpertRestrictionResponse get(
            @PathVariable
            UUID expertBusinessKey,

            @PathVariable
            UUID restrictionBusinessKey
    ) {
        return service.get(
                expertBusinessKey,
                restrictionBusinessKey
        );
    }

    @PutMapping("/{restrictionBusinessKey}")
    public ExpertRestrictionResponse update(
            @PathVariable
            UUID expertBusinessKey,

            @PathVariable
            UUID restrictionBusinessKey,

            @Valid
            @RequestBody
            UpdateExpertRestrictionRequest request
    ) {
        return service.update(
                expertBusinessKey,
                restrictionBusinessKey,
                request
        );
    }

    @PatchMapping("/{restrictionBusinessKey}/status")
    public ExpertRestrictionResponse changeStatus(
            @PathVariable
            UUID expertBusinessKey,

            @PathVariable
            UUID restrictionBusinessKey,

            @RequestBody
            ChangeExpertRestrictionStatusRequest request
    ) {
        return service.changeStatus(
                expertBusinessKey,
                restrictionBusinessKey,
                request
        );
    }
}
