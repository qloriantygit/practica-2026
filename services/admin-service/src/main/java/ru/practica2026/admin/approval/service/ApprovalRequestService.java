package ru.practica2026.admin.approval.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.approval.dto.response.ApprovalPageResponse;
import ru.practica2026.admin.approval.dto.response.ApprovalResponse;
import ru.practica2026.admin.approval.entity.ApprovalRequest;
import ru.practica2026.admin.approval.entity.ApprovalResourceType;
import ru.practica2026.admin.approval.entity.ApprovalStatus;
import ru.practica2026.admin.approval.mapper.ApprovalMapper;
import ru.practica2026.admin.approval.repository.ApprovalRequestRepository;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;

import ru.practica2026.admin.security.service.CurrentActorService;

import java.time.Instant;
import java.util.UUID;

@Service
public class ApprovalRequestService {

    private final ApprovalRequestRepository repository;
    private final CurrentActorService currentActorService;
    private final ApprovalNotificationService notificationService;

    public ApprovalRequestService(
            ApprovalRequestRepository repository,
            CurrentActorService currentActorService,
            ApprovalNotificationService notificationService
    ) {
        this.repository = repository;
        this.currentActorService = currentActorService;
        this.notificationService = notificationService;
    }

    @Transactional
    public ApprovalResponse createDirectoryVersionApproval(
            UUID versionBusinessKey
    ) {
        if (
                repository
                        .existsByResourceTypeAndResourceKeyAndStatus(
                                ApprovalResourceType.DIRECTORY_VERSION,
                                versionBusinessKey,
                                ApprovalStatus.PENDING
                        )
        ) {
            throw new ConflictException(
                    "Directory version already has a pending approval request"
            );
        }

        String actor =
                currentActorService
                        .getCurrentActor();

        ApprovalRequest approval =
                new ApprovalRequest();

        approval.setResourceType(
                ApprovalResourceType.DIRECTORY_VERSION
        );

        approval.setResourceKey(
                versionBusinessKey
        );

        approval.setStatus(
                ApprovalStatus.PENDING
        );

        approval.setRequestedBy(actor);
        approval.setRequestedAt(Instant.now());

        approval.setCreatedBy(actor);
        approval.setUpdatedBy(actor);

        ApprovalRequest saved =
                repository.saveAndFlush(
                        approval
                );

        notificationService.notifySubmitted(
                saved
        );

        return ApprovalMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ApprovalPageResponse findAll(
            ApprovalStatus status,
            ApprovalResourceType resourceType,
            int page,
            int size
    ) {
        PageRequest pageable =
                PageRequest.of(
                        Math.max(page, 0),
                        Math.min(
                                Math.max(size, 1),
                                100
                        ),
                        Sort.by(
                                Sort.Direction.DESC,
                                "requestedAt"
                        )
                );

        Page<ApprovalRequest> result;

        if (
                status != null
                &&
                resourceType != null
        ) {
            result =
                    repository
                            .findAllByStatusAndResourceType(
                                    status,
                                    resourceType,
                                    pageable
                            );
        }
        else if (status != null) {
            result =
                    repository.findAllByStatus(
                            status,
                            pageable
                    );
        }
        else if (resourceType != null) {
            result =
                    repository.findAllByResourceType(
                            resourceType,
                            pageable
                    );
        }
        else {
            result =
                    repository.findAll(pageable);
        }

        return new ApprovalPageResponse(
                result.getContent()
                        .stream()
                        .map(ApprovalMapper::toResponse)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public ApprovalResponse findOne(
            UUID businessKey
    ) {
        return ApprovalMapper.toResponse(
                getEntity(businessKey)
        );
    }

    @Transactional(readOnly = true)
    public ApprovalRequest getPendingEntity(
            UUID businessKey
    ) {
        ApprovalRequest approval =
                getEntity(businessKey);

        if (
                approval.getStatus()
                        != ApprovalStatus.PENDING
        ) {
            throw new ConflictException(
                    "Only PENDING approval request can be decided"
            );
        }

        return approval;
    }

    @Transactional
    public ApprovalResponse markApproved(
            ApprovalRequest approval,
            String comment
    ) {
        return complete(
                approval,
                ApprovalStatus.APPROVED,
                comment
        );
    }

    @Transactional
    public ApprovalResponse markRejected(
            ApprovalRequest approval,
            String comment
    ) {
        return complete(
                approval,
                ApprovalStatus.REJECTED,
                comment
        );
    }

    private ApprovalResponse complete(
            ApprovalRequest approval,
            ApprovalStatus status,
            String comment
    ) {
        String actor =
                currentActorService
                        .getCurrentActor();

        approval.setStatus(status);
        approval.setDecidedBy(actor);
        approval.setDecidedAt(Instant.now());

        approval.setDecisionComment(
                normalizeNullable(comment)
        );

        approval.setUpdatedBy(actor);

        repository.flush();

        notificationService.notifyDecision(
                approval
        );

        return ApprovalMapper.toResponse(
                approval
        );
    }

    private ApprovalRequest getEntity(
            UUID businessKey
    ) {
        return repository
                .findByBusinessKey(
                        businessKey
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Approval request not found"
                                )
                );
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
}
