package ru.practica2026.admin.approval.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practica2026.admin.approval.dto.response.ApprovalResponse;
import ru.practica2026.admin.approval.entity.ApprovalRequest;
import ru.practica2026.admin.approval.entity.ApprovalResourceType;
import ru.practica2026.admin.approval.entity.ApprovalStatus;
import ru.practica2026.admin.approval.repository.ApprovalRequestRepository;

import ru.practica2026.admin.common.exception.ConflictException;

import ru.practica2026.admin.security.service.CurrentActorService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprovalRequestServiceTest {

    @Mock
    private ApprovalRequestRepository repository;

    @Mock
    private CurrentActorService currentActorService;

    @Mock
    private ApprovalNotificationService notificationService;

    @InjectMocks
    private ApprovalRequestService service;

    @Test
    void createDirectoryVersionApprovalCreatesPendingApproval() {
        UUID versionBusinessKey =
                UUID.randomUUID();

        when(
                repository
                        .existsByResourceTypeAndResourceKeyAndStatus(
                                ApprovalResourceType.DIRECTORY_VERSION,
                                versionBusinessKey,
                                ApprovalStatus.PENDING
                        )
        ).thenReturn(false);

        when(
                currentActorService
                        .getCurrentActor()
        ).thenReturn("test.admin");

        when(
                repository.saveAndFlush(
                        any(ApprovalRequest.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        ApprovalResponse response =
                service.createDirectoryVersionApproval(
                        versionBusinessKey
                );

        assertEquals(
                ApprovalResourceType.DIRECTORY_VERSION,
                response.resourceType()
        );

        assertEquals(
                versionBusinessKey,
                response.resourceKey()
        );

        assertEquals(
                ApprovalStatus.PENDING,
                response.status()
        );

        assertEquals(
                "test.admin",
                response.requestedBy()
        );

        assertNotNull(
                response.requestedAt()
        );

        ArgumentCaptor<ApprovalRequest> captor =
                ArgumentCaptor.forClass(
                        ApprovalRequest.class
                );

        verify(repository)
                .saveAndFlush(
                        captor.capture()
                );

        ApprovalRequest saved =
                captor.getValue();

        assertEquals(
                ApprovalStatus.PENDING,
                saved.getStatus()
        );

        assertEquals(
                "test.admin",
                saved.getCreatedBy()
        );

        verify(notificationService)
                .notifySubmitted(saved);
    }

    @Test
    void createDirectoryVersionApprovalRejectsDuplicatePendingApproval() {
        UUID versionBusinessKey =
                UUID.randomUUID();

        when(
                repository
                        .existsByResourceTypeAndResourceKeyAndStatus(
                                ApprovalResourceType.DIRECTORY_VERSION,
                                versionBusinessKey,
                                ApprovalStatus.PENDING
                        )
        ).thenReturn(true);

        assertThrows(
                ConflictException.class,
                () ->
                        service
                                .createDirectoryVersionApproval(
                                        versionBusinessKey
                                )
        );

        verify(
                repository,
                never()
        ).saveAndFlush(
                any(ApprovalRequest.class)
        );

        verify(
                notificationService,
                never()
        ).notifySubmitted(
                any(ApprovalRequest.class)
        );
    }
}
