package ru.practica2026.admin.approval.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.approval.dto.request.ApprovalDecisionRequest;
import ru.practica2026.admin.approval.dto.response.ApprovalResponse;
import ru.practica2026.admin.approval.entity.ApprovalRequest;
import ru.practica2026.admin.approval.entity.ApprovalResourceType;

import ru.practica2026.admin.common.exception.ConflictException;

import ru.practica2026.admin.directory.service.DirectoryLifecycleService;

import java.util.UUID;

@Service
public class ApprovalWorkflowService {

    private final ApprovalRequestService approvalRequestService;
    private final DirectoryLifecycleService directoryLifecycleService;

    public ApprovalWorkflowService(
            ApprovalRequestService approvalRequestService,
            DirectoryLifecycleService directoryLifecycleService
    ) {
        this.approvalRequestService =
                approvalRequestService;

        this.directoryLifecycleService =
                directoryLifecycleService;
    }

    @Transactional
    public ApprovalResponse approve(
            UUID approvalBusinessKey,
            ApprovalDecisionRequest request
    ) {
        ApprovalRequest approval =
                approvalRequestService
                        .getPendingEntity(
                                approvalBusinessKey
                        );

        ensureDirectoryVersion(approval);

        directoryLifecycleService.publish(
                approval.getResourceKey()
        );

        return approvalRequestService
                .markApproved(
                        approval,
                        comment(request)
                );
    }

    @Transactional
    public ApprovalResponse reject(
            UUID approvalBusinessKey,
            ApprovalDecisionRequest request
    ) {
        ApprovalRequest approval =
                approvalRequestService
                        .getPendingEntity(
                                approvalBusinessKey
                        );

        ensureDirectoryVersion(approval);

        directoryLifecycleService
                .returnToDraftFromApproval(
                        approval.getResourceKey()
                );

        return approvalRequestService
                .markRejected(
                        approval,
                        comment(request)
                );
    }

    private void ensureDirectoryVersion(
            ApprovalRequest approval
    ) {
        if (
                approval.getResourceType()
                        != ApprovalResourceType.DIRECTORY_VERSION
        ) {
            throw new ConflictException(
                    "Unsupported approval resource type"
            );
        }
    }

    private String comment(
            ApprovalDecisionRequest request
    ) {
        return request == null
                ? null
                : request.comment();
    }
}
