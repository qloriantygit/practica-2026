package ru.practica2026.admin.approval.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practica2026.admin.approval.dto.request.ApprovalDecisionRequest;
import ru.practica2026.admin.approval.entity.ApprovalRequest;
import ru.practica2026.admin.approval.entity.ApprovalResourceType;
import ru.practica2026.admin.approval.entity.ApprovalStatus;

import ru.practica2026.admin.directory.service.DirectoryLifecycleService;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprovalWorkflowServiceTest {

    @Mock
    private ApprovalRequestService approvalRequestService;

    @Mock
    private DirectoryLifecycleService directoryLifecycleService;

    @InjectMocks
    private ApprovalWorkflowService service;

    @Test
    void approvePublishesDirectoryAndCompletesApproval() {
        UUID approvalBusinessKey =
                UUID.randomUUID();

        UUID versionBusinessKey =
                UUID.randomUUID();

        ApprovalRequest approval =
                pendingApproval(
                        versionBusinessKey
                );

        when(
                approvalRequestService
                        .getPendingEntity(
                                approvalBusinessKey
                        )
        ).thenReturn(approval);

        ApprovalDecisionRequest request =
                new ApprovalDecisionRequest(
                        "Approved"
                );

        service.approve(
                approvalBusinessKey,
                request
        );

        verify(directoryLifecycleService)
                .publish(
                        versionBusinessKey
                );

        verify(approvalRequestService)
                .markApproved(
                        approval,
                        "Approved"
                );
    }

    @Test
    void rejectReturnsDirectoryToDraftAndCompletesApproval() {
        UUID approvalBusinessKey =
                UUID.randomUUID();

        UUID versionBusinessKey =
                UUID.randomUUID();

        ApprovalRequest approval =
                pendingApproval(
                        versionBusinessKey
                );

        when(
                approvalRequestService
                        .getPendingEntity(
                                approvalBusinessKey
                        )
        ).thenReturn(approval);

        ApprovalDecisionRequest request =
                new ApprovalDecisionRequest(
                        "Needs correction"
                );

        service.reject(
                approvalBusinessKey,
                request
        );

        verify(directoryLifecycleService)
                .returnToDraftFromApproval(
                        versionBusinessKey
                );

        verify(approvalRequestService)
                .markRejected(
                        approval,
                        "Needs correction"
                );
    }

    private ApprovalRequest pendingApproval(
            UUID versionBusinessKey
    ) {
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

        return approval;
    }
}
