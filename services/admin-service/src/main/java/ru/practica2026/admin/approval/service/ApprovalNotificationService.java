package ru.practica2026.admin.approval.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.approval.entity.ApprovalRequest;
import ru.practica2026.admin.outbox.service.CurrentCorrelationIdService;
import ru.practica2026.admin.outbox.service.OutboxEventService;
import ru.practica2026.admin.user.entity.UserAccount;
import ru.practica2026.admin.user.repository.UserAccountRepository;

@Service
public class ApprovalNotificationService {

    private final UserAccountRepository userRepository;
    private final OutboxEventService outboxEventService;
    private final CurrentCorrelationIdService correlationIdService;

    public ApprovalNotificationService(
            UserAccountRepository userRepository,
            OutboxEventService outboxEventService,
            CurrentCorrelationIdService correlationIdService
    ) {
        this.userRepository =
                userRepository;

        this.outboxEventService =
                outboxEventService;

        this.correlationIdService =
                correlationIdService;
    }

    @Transactional
    public void notifySubmitted(
            ApprovalRequest approval
    ) {
        String recipient =
                resolveEmail(
                        approval.getRequestedBy()
                );

        outboxEventService.enqueueNotification(
                correlationIdService
                        .getCurrentCorrelationId(),
                approval.getRequestedBy(),
                approval.getBusinessKey()
                        .toString(),
                recipient,
                "Approval request submitted",
                "Directory version "
                        + approval.getResourceKey()
                        + " was submitted for approval."
        );
    }

    @Transactional
    public void notifyDecision(
            ApprovalRequest approval
    ) {
        String recipient =
                resolveEmail(
                        approval.getRequestedBy()
                );

        String actor =
                approval.getDecidedBy() != null
                        ? approval.getDecidedBy()
                        : approval.getRequestedBy();

        String subject =
                "Approval request "
                        + approval.getStatus()
                                .name()
                                .toLowerCase();

        String body =
                "Approval request "
                        + approval.getBusinessKey()
                        + " for directory version "
                        + approval.getResourceKey()
                        + " was "
                        + approval.getStatus()
                                .name()
                                .toLowerCase()
                        + "."
                        + (
                                approval.getDecisionComment() != null
                                        ? " Comment: "
                                            + approval.getDecisionComment()
                                        : ""
                        );

        outboxEventService.enqueueNotification(
                correlationIdService
                        .getCurrentCorrelationId(),
                actor,
                approval.getBusinessKey()
                        .toString(),
                recipient,
                subject,
                body
        );
    }

    private String resolveEmail(
            String username
    ) {
        UserAccount user =
                userRepository
                        .findFirstByUsernameIgnoreCase(
                                username
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "User for notification was not found: "
                                                        + username
                                        )
                        );

        if (
                user.getEmail() == null
                ||
                user.getEmail().isBlank()
        ) {
            throw new IllegalStateException(
                    "User email is empty: "
                            + username
            );
        }

        return user.getEmail();
    }
}
