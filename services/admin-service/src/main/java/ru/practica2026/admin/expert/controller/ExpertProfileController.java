package ru.practica2026.admin.expert.controller;

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

import ru.practica2026.admin.expert.dto.request.AddExpertCompetencyRequest;
import ru.practica2026.admin.expert.dto.request.CreateExpertProfileRequest;
import ru.practica2026.admin.expert.dto.request.UpdateExpertProfileRequest;
import ru.practica2026.admin.expert.dto.response.ExpertCompetencyResponse;
import ru.practica2026.admin.expert.dto.response.ExpertProfilePageResponse;
import ru.practica2026.admin.expert.dto.response.ExpertProfileResponse;
import ru.practica2026.admin.expert.entity.ExpertProfileStatus;
import ru.practica2026.admin.expert.service.ExpertProfileService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experts")
public class ExpertProfileController {

    private final ExpertProfileService expertProfileService;

    public ExpertProfileController(
            ExpertProfileService expertProfileService
    ) {
        this.expertProfileService =
                expertProfileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpertProfileResponse create(
            @Valid
            @RequestBody
            CreateExpertProfileRequest request
    ) {
        return expertProfileService.create(
                request
        );
    }

    @GetMapping
    public ExpertProfilePageResponse findAll(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            ExpertProfileStatus status,

            @RequestParam(required = false)
            Boolean available,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(defaultValue = "specialization")
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            String direction
    ) {
        return expertProfileService.findAll(
                search,
                status,
                available,
                page,
                size,
                sortBy,
                direction
        );
    }

    @GetMapping("/{businessKey}")
    public ExpertProfileResponse findByBusinessKey(
            @PathVariable
            UUID businessKey
    ) {
        return expertProfileService
                .findByBusinessKey(
                        businessKey
                );
    }

    @PutMapping("/{businessKey}")
    public ExpertProfileResponse update(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            UpdateExpertProfileRequest request
    ) {
        return expertProfileService.update(
                businessKey,
                request
        );
    }

    @PostMapping(
            "/{businessKey}/competencies"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ExpertCompetencyResponse addCompetency(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            AddExpertCompetencyRequest request
    ) {
        return expertProfileService
                .addCompetency(
                        businessKey,
                        request
                );
    }

    @DeleteMapping(
            "/{businessKey}/competencies/{competencyBusinessKey}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompetency(
            @PathVariable
            UUID businessKey,

            @PathVariable
            UUID competencyBusinessKey
    ) {
        expertProfileService
                .deleteCompetency(
                        businessKey,
                        competencyBusinessKey
                );
    }
}
