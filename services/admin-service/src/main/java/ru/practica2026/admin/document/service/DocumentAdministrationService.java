package ru.practica2026.admin.document.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;
import ru.practica2026.admin.document.dto.request.SaveDocumentTypeRequest;
import ru.practica2026.admin.document.dto.request.SaveMandatoryDocumentRuleRequest;
import ru.practica2026.admin.document.dto.response.DocumentTypePageResponse;
import ru.practica2026.admin.document.dto.response.DocumentTypeResponse;
import ru.practica2026.admin.document.dto.response.MandatoryDocumentRulePageResponse;
import ru.practica2026.admin.document.dto.response.MandatoryDocumentRuleResponse;
import ru.practica2026.admin.document.entity.DocumentType;
import ru.practica2026.admin.document.entity.MandatoryDocumentRule;
import ru.practica2026.admin.document.repository.DocumentTypeRepository;
import ru.practica2026.admin.document.repository.MandatoryDocumentRuleRepository;
import ru.practica2026.admin.security.service.CurrentActorService;

import java.util.Locale;
import java.util.UUID;

@Service
public class DocumentAdministrationService {

    private final DocumentTypeRepository typeRepository;
    private final MandatoryDocumentRuleRepository ruleRepository;
    private final CurrentActorService currentActorService;

    public DocumentAdministrationService(
            DocumentTypeRepository typeRepository,
            MandatoryDocumentRuleRepository ruleRepository,
            CurrentActorService currentActorService
    ) {
        this.typeRepository = typeRepository;
        this.ruleRepository = ruleRepository;
        this.currentActorService =
                currentActorService;
    }

    @Transactional
    public DocumentTypeResponse createType(
            SaveDocumentTypeRequest request
    ) {
        String code =
                normalizeCode(request.code());

        if (
                typeRepository
                        .existsByCodeIgnoreCase(code)
        ) {
            throw new ConflictException(
                    "Document type with code '"
                            + code
                            + "' already exists"
            );
        }

        String actor =
                currentActorService.getCurrentActor();

        DocumentType type =
                new DocumentType();

        type.setCode(code);
        type.setName(
                request.name().trim()
        );
        type.setDescription(
                normalizeNullable(
                        request.description()
                )
        );
        type.setActive(
                request.active() == null
                        || request.active()
        );
        type.setCreatedBy(actor);
        type.setUpdatedBy(actor);

        DocumentType saved =
                typeRepository
                        .saveAndFlush(type);

        return toTypeResponse(saved);
    }

    @Transactional(readOnly = true)
    public DocumentTypePageResponse findTypes(
            String search,
            Boolean active,
            int page,
            int size
    ) {
        Page<DocumentType> result =
                typeRepository.search(
                        normalizeSearch(search),
                        active,
                        PageRequest.of(
                                Math.max(page, 0),
                                Math.min(
                                        Math.max(size, 1),
                                        100
                                ),
                                Sort.by(
                                        Sort.Direction.ASC,
                                        "code"
                                )
                        )
                );

        return new DocumentTypePageResponse(
                result.getContent()
                        .stream()
                        .map(this::toTypeResponse)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public DocumentTypeResponse findType(
            UUID businessKey
    ) {
        return toTypeResponse(
                getType(businessKey)
        );
    }

    @Transactional
    public DocumentTypeResponse updateType(
            UUID businessKey,
            SaveDocumentTypeRequest request
    ) {
        DocumentType type =
                getType(businessKey);

        String code =
                normalizeCode(request.code());

        if (
                !type.getCode()
                        .equalsIgnoreCase(code)
                &&
                typeRepository
                        .existsByCodeIgnoreCase(code)
        ) {
            throw new ConflictException(
                    "Document type with code '"
                            + code
                            + "' already exists"
            );
        }

        type.setCode(code);
        type.setName(
                request.name().trim()
        );
        type.setDescription(
                normalizeNullable(
                        request.description()
                )
        );

        if (request.active() != null) {
            type.setActive(request.active());
        }

        type.setUpdatedBy(
                currentActorService.getCurrentActor()
        );

        typeRepository.flush();

        return toTypeResponse(type);
    }

    @Transactional
    public MandatoryDocumentRuleResponse createRule(
            SaveMandatoryDocumentRuleRequest request
    ) {
        DocumentType documentType =
                getType(
                        request.documentTypeBusinessKey()
                );

        if (!documentType.isActive()) {
            throw new ConflictException(
                    "Mandatory rule cannot reference inactive document type"
            );
        }

        String contextCode =
                normalizeCode(
                        request.contextCode()
                );

        if (
                ruleRepository
                        .existsByContextCodeIgnoreCaseAndDocumentType(
                                contextCode,
                                documentType
                        )
        ) {
            throw new ConflictException(
                    "Mandatory document rule already exists"
            );
        }

        String actor =
                currentActorService.getCurrentActor();

        MandatoryDocumentRule rule =
                new MandatoryDocumentRule();

        rule.setContextCode(contextCode);
        rule.setDocumentType(documentType);
        rule.setMandatory(
                request.mandatory()
        );
        rule.setActive(
                request.active() == null
                        || request.active()
        );
        rule.setCreatedBy(actor);
        rule.setUpdatedBy(actor);

        MandatoryDocumentRule saved =
                ruleRepository
                        .saveAndFlush(rule);

        return toRuleResponse(saved);
    }

    @Transactional(readOnly = true)
    public MandatoryDocumentRulePageResponse findRules(
            String contextCode,
            Boolean mandatory,
            Boolean active,
            int page,
            int size
    ) {
        String normalizedContext =
                contextCode == null
                        || contextCode.isBlank()
                        ? null
                        : contextCode
                                .trim()
                                .toLowerCase(
                                        Locale.ROOT
                                );

        Page<MandatoryDocumentRule> result =
                ruleRepository.search(
                        normalizedContext,
                        mandatory,
                        active,
                        PageRequest.of(
                                Math.max(page, 0),
                                Math.min(
                                        Math.max(size, 1),
                                        100
                                ),
                                Sort.by(
                                        Sort.Direction.ASC,
                                        "contextCode"
                                )
                        )
                );

        return new MandatoryDocumentRulePageResponse(
                result.getContent()
                        .stream()
                        .map(this::toRuleResponse)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional
    public MandatoryDocumentRuleResponse updateRule(
            UUID businessKey,
            SaveMandatoryDocumentRuleRequest request
    ) {
        MandatoryDocumentRule rule =
                ruleRepository
                        .findByBusinessKey(
                                businessKey
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Mandatory document rule not found: "
                                                        + businessKey
                                        )
                        );

        DocumentType documentType =
                getType(
                        request.documentTypeBusinessKey()
                );

        rule.setContextCode(
                normalizeCode(
                        request.contextCode()
                )
        );
        rule.setDocumentType(documentType);
        rule.setMandatory(
                request.mandatory()
        );

        if (request.active() != null) {
            rule.setActive(request.active());
        }

        rule.setUpdatedBy(
                currentActorService.getCurrentActor()
        );

        ruleRepository.flush();

        return toRuleResponse(rule);
    }

    private DocumentType getType(
            UUID businessKey
    ) {
        return typeRepository
                .findByBusinessKey(businessKey)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Document type not found: "
                                                + businessKey
                                )
                );
    }

    private DocumentTypeResponse toTypeResponse(
            DocumentType type
    ) {
        return new DocumentTypeResponse(
                type.getBusinessKey(),
                type.getCode(),
                type.getName(),
                type.getDescription(),
                type.isActive(),
                type.getVersion(),
                type.getCreatedAt(),
                type.getUpdatedAt(),
                type.getCreatedBy(),
                type.getUpdatedBy()
        );
    }

    private MandatoryDocumentRuleResponse toRuleResponse(
            MandatoryDocumentRule rule
    ) {
        return new MandatoryDocumentRuleResponse(
                rule.getBusinessKey(),
                rule.getContextCode(),
                rule.getDocumentType()
                        .getBusinessKey(),
                rule.getDocumentType()
                        .getCode(),
                rule.getDocumentType()
                        .getName(),
                rule.isMandatory(),
                rule.isActive(),
                rule.getVersion(),
                rule.getCreatedAt(),
                rule.getUpdatedAt(),
                rule.getCreatedBy(),
                rule.getUpdatedBy()
        );
    }

    private String normalizeCode(
            String value
    ) {
        return value
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeNullable(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }

    private String normalizeSearch(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        return "%"
                + normalized
                        .toLowerCase(Locale.ROOT)
                + "%";
    }
}
