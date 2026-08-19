package ru.practica2026.admin.approval.mapper;

import ru.practica2026.admin.approval.dto.response.ApprovalResponse;
import ru.practica2026.admin.approval.entity.ApprovalRequest;

public final class ApprovalMapper {

    private ApprovalMapper() {
    }

    public static ApprovalResponse toResponse(
            ApprovalRequest approval
    ) {
        return new ApprovalResponse(
                approval.getBusinessKey(),
                approval.getResourceType(),
                approval.getResourceKey(),
                approval.getStatus(),
                approval.getRequestedBy(),
                approval.getRequestedAt(),
                approval.getDecidedBy(),
                approval.getDecidedAt(),
                approval.getDecisionComment(),
                approval.getVersion(),
                approval.getCreatedAt(),
                approval.getUpdatedAt()
        );
    }
}
