package ru.practica2026.admin.audit.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.audit.dto.AdminAuditLogPageResponse;
import ru.practica2026.admin.audit.dto.AdminAuditLogResponse;
import ru.practica2026.admin.audit.entity.AdminAuditLog;
import ru.practica2026.admin.audit.repository.AdminAuditLogRepository;

import java.util.Locale;

@Service
public class AdminAuditService {

    private final AdminAuditLogRepository repository;

    public AdminAuditService(
            AdminAuditLogRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void record(
            String correlationId,
            String actor,
            String method,
            String path,
            String entityType,
            String entityKey,
            String beforeState,
            String afterState,
            boolean success,
            String errorMessage
    ) {
        AdminAuditLog audit =
                new AdminAuditLog();

        audit.setCorrelationId(correlationId);
        audit.setActor(actor);
        audit.setHttpMethod(method);
        audit.setRequestPath(path);

        audit.setAction(
                method + " " + path
        );

        audit.setEntityType(entityType);
        audit.setEntityKey(entityKey);

        audit.setBeforeState(beforeState);
        audit.setAfterState(afterState);

        audit.setSuccess(success);
        audit.setErrorMessage(errorMessage);

        audit.setCreatedBy(actor);
        audit.setUpdatedBy(actor);

        repository.saveAndFlush(audit);
    }

    @Transactional(readOnly = true)
    public AdminAuditLogPageResponse findAll(
            String search,
            Boolean success,
            int page,
            int size
    ) {
        Page<AdminAuditLog> result =
                repository.search(
                        normalizeSearch(search),
                        success,
                        PageRequest.of(
                                Math.max(page, 0),
                                Math.min(
                                        Math.max(size, 1),
                                        100
                                ),
                                Sort.by(
                                        Sort.Direction.DESC,
                                        "createdAt"
                                )
                        )
                );

        return new AdminAuditLogPageResponse(
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

    private AdminAuditLogResponse toResponse(
            AdminAuditLog audit
    ) {
        return new AdminAuditLogResponse(
                audit.getBusinessKey(),
                audit.getCorrelationId(),
                audit.getActor(),
                audit.getHttpMethod(),
                audit.getRequestPath(),
                audit.getAction(),
                audit.getEntityType(),
                audit.getEntityKey(),
                audit.getBeforeState(),
                audit.getAfterState(),
                audit.isSuccess(),
                audit.getErrorMessage(),
                audit.getCreatedAt()
        );
    }

    private String normalizeSearch(
            String value
    ) {
        if (
                value == null
                ||
                value.isBlank()
        ) {
            return null;
        }

        return "%"
                + value.trim()
                        .toLowerCase(Locale.ROOT)
                + "%";
    }
}
