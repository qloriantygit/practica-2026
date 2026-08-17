package ru.practica2026.admin.template.controller;

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

import ru.practica2026.admin.template.dto.request.SaveTemplateRequest;
import ru.practica2026.admin.template.dto.response.TemplatePageResponse;
import ru.practica2026.admin.template.dto.response.TemplateResponse;
import ru.practica2026.admin.template.entity.TemplateType;
import ru.practica2026.admin.template.service.TemplateService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {

    private final TemplateService service;

    public TemplateController(
            TemplateService service
    ) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TemplateResponse create(
            @Valid
            @RequestBody
            SaveTemplateRequest request
    ) {
        return service.create(request);
    }

    @GetMapping
    public TemplatePageResponse findAll(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            TemplateType type,

            @RequestParam(required = false)
            Boolean active,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        return service.findAll(
                search,
                type,
                active,
                page,
                size
        );
    }

    @GetMapping("/{businessKey}")
    public TemplateResponse findOne(
            @PathVariable
            UUID businessKey
    ) {
        return service.findByBusinessKey(
                businessKey
        );
    }

    @PutMapping("/{businessKey}")
    public TemplateResponse update(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            SaveTemplateRequest request
    ) {
        return service.update(
                businessKey,
                request
        );
    }
}
