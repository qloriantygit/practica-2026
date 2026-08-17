package ru.practica2026.admin.document.controller;

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

import ru.practica2026.admin.document.dto.request.SaveDocumentTypeRequest;
import ru.practica2026.admin.document.dto.request.SaveMandatoryDocumentRuleRequest;
import ru.practica2026.admin.document.dto.response.DocumentTypePageResponse;
import ru.practica2026.admin.document.dto.response.DocumentTypeResponse;
import ru.practica2026.admin.document.dto.response.MandatoryDocumentRulePageResponse;
import ru.practica2026.admin.document.dto.response.MandatoryDocumentRuleResponse;
import ru.practica2026.admin.document.service.DocumentAdministrationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class DocumentAdministrationController {

    private final DocumentAdministrationService service;

    public DocumentAdministrationController(
            DocumentAdministrationService service
    ) {
        this.service = service;
    }

    @PostMapping("/document-types")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentTypeResponse createType(
            @Valid
            @RequestBody
            SaveDocumentTypeRequest request
    ) {
        return service.createType(request);
    }

    @GetMapping("/document-types")
    public DocumentTypePageResponse findTypes(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            Boolean active,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        return service.findTypes(
                search,
                active,
                page,
                size
        );
    }

    @GetMapping("/document-types/{businessKey}")
    public DocumentTypeResponse findType(
            @PathVariable
            UUID businessKey
    ) {
        return service.findType(businessKey);
    }

    @PutMapping("/document-types/{businessKey}")
    public DocumentTypeResponse updateType(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            SaveDocumentTypeRequest request
    ) {
        return service.updateType(
                businessKey,
                request
        );
    }

    @PostMapping("/document-rules")
    @ResponseStatus(HttpStatus.CREATED)
    public MandatoryDocumentRuleResponse createRule(
            @Valid
            @RequestBody
            SaveMandatoryDocumentRuleRequest request
    ) {
        return service.createRule(request);
    }

    @GetMapping("/document-rules")
    public MandatoryDocumentRulePageResponse findRules(
            @RequestParam(required = false)
            String contextCode,

            @RequestParam(required = false)
            Boolean mandatory,

            @RequestParam(required = false)
            Boolean active,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        return service.findRules(
                contextCode,
                mandatory,
                active,
                page,
                size
        );
    }

    @PutMapping("/document-rules/{businessKey}")
    public MandatoryDocumentRuleResponse updateRule(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            SaveMandatoryDocumentRuleRequest request
    ) {
        return service.updateRule(
                businessKey,
                request
        );
    }
}
