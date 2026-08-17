package ru.practica2026.admin.template.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;
import ru.practica2026.admin.security.service.CurrentActorService;
import ru.practica2026.admin.template.dto.request.SaveTemplateRequest;
import ru.practica2026.admin.template.dto.response.TemplatePageResponse;
import ru.practica2026.admin.template.dto.response.TemplateResponse;
import ru.practica2026.admin.template.entity.AdminTemplate;
import ru.practica2026.admin.template.entity.TemplateType;
import ru.practica2026.admin.template.repository.AdminTemplateRepository;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.UUID;

@Service
public class TemplateService {

    private final AdminTemplateRepository repository;
    private final CurrentActorService currentActorService;

    public TemplateService(
            AdminTemplateRepository repository,
            CurrentActorService currentActorService
    ) {
        this.repository = repository;
        this.currentActorService =
                currentActorService;
    }

    @Transactional
    public TemplateResponse create(
            SaveTemplateRequest request
    ) {
        String code =
                normalizeCode(request.code());

        if (repository.existsByCodeIgnoreCase(code)) {
            throw new ConflictException(
                    "Template with code '"
                            + code
                            + "' already exists"
            );
        }

        validateTemplate(request);

        String actor =
                currentActorService.getCurrentActor();

        AdminTemplate template =
                new AdminTemplate();

        apply(
                template,
                request,
                code
        );

        template.setCreatedBy(actor);
        template.setUpdatedBy(actor);

        AdminTemplate saved =
                repository.saveAndFlush(template);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TemplatePageResponse findAll(
            String search,
            TemplateType type,
            Boolean active,
            int page,
            int size
    ) {
        Page<AdminTemplate> result =
                repository.search(
                        normalizeSearch(search),
                        type,
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

        return new TemplatePageResponse(
                result.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public TemplateResponse findByBusinessKey(
            UUID businessKey
    ) {
        return toResponse(
                getTemplate(businessKey)
        );
    }

    @Transactional
    public TemplateResponse update(
            UUID businessKey,
            SaveTemplateRequest request
    ) {
        AdminTemplate template =
                getTemplate(businessKey);

        String code =
                normalizeCode(request.code());

        if (
                !template.getCode()
                        .equalsIgnoreCase(code)
                &&
                repository.existsByCodeIgnoreCase(code)
        ) {
            throw new ConflictException(
                    "Template with code '"
                            + code
                            + "' already exists"
            );
        }

        validateTemplate(request);

        apply(
                template,
                request,
                code
        );

        template.setUpdatedBy(
                currentActorService.getCurrentActor()
        );

        repository.flush();

        return toResponse(template);
    }

    private void validateTemplate(
            SaveTemplateRequest request
    ) {
        if (
                request.templateType()
                        == TemplateType.NOTIFICATION
                &&
                request.channel() == null
        ) {
            throw new ConflictException(
                    "Notification template requires channel"
            );
        }

        if (
                request.templateType()
                        == TemplateType.DOCUMENT
                &&
                request.channel() != null
        ) {
            throw new ConflictException(
                    "Document template cannot have notification channel"
            );
        }
    }

    private void apply(
            AdminTemplate template,
            SaveTemplateRequest request,
            String code
    ) {
        template.setCode(code);
        template.setName(
                request.name().trim()
        );
        template.setDescription(
                normalizeNullable(
                        request.description()
                )
        );
        template.setTemplateType(
                request.templateType()
        );
        template.setChannel(
                request.channel()
        );
        template.setSubject(
                normalizeNullable(
                        request.subject()
                )
        );
        template.setBody(
                request.body().trim()
        );
        template.setVariables(
                request.variables() == null
                        ? new LinkedHashSet<>()
                        : request.variables()
        );
        template.setActive(
                request.active() == null
                        || request.active()
        );
    }

    private TemplateResponse toResponse(
            AdminTemplate template
    ) {
        return new TemplateResponse(
                template.getBusinessKey(),
                template.getCode(),
                template.getName(),
                template.getDescription(),
                template.getTemplateType(),
                template.getChannel(),
                template.getSubject(),
                template.getBody(),
                new LinkedHashSet<>(
                        template.getVariables()
                ),
                template.isActive(),
                template.getVersion(),
                template.getCreatedAt(),
                template.getUpdatedAt(),
                template.getCreatedBy(),
                template.getUpdatedBy()
        );
    }

    private AdminTemplate getTemplate(
            UUID businessKey
    ) {
        return repository
                .findByBusinessKey(businessKey)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Template not found: "
                                                + businessKey
                                )
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

        String normalized = value.trim();

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

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        return "%"
                + normalized
                        .toLowerCase(Locale.ROOT)
                + "%";
    }
}
