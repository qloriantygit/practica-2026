package ru.practica2026.admin.approval.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import ru.practica2026.admin.common.entity.BaseEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "approval_requests")
public class ApprovalRequest extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(
            name = "resource_type",
            nullable = false,
            length = 64
    )
    private ApprovalResourceType resourceType;

    @Column(
            name = "resource_key",
            nullable = false
    )
    private UUID resourceKey;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 32
    )
    private ApprovalStatus status;

    @Column(
            name = "requested_by",
            nullable = false,
            length = 255
    )
    private String requestedBy;

    @Column(
            name = "requested_at",
            nullable = false
    )
    private Instant requestedAt;

    @Column(
            name = "decided_by",
            length = 255
    )
    private String decidedBy;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @Column(name = "decision_comment")
    private String decisionComment;

    public ApprovalResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(
            ApprovalResourceType resourceType
    ) {
        this.resourceType = resourceType;
    }

    public UUID getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(
            UUID resourceKey
    ) {
        this.resourceKey = resourceKey;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(
            ApprovalStatus status
    ) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(
            String requestedBy
    ) {
        this.requestedBy = requestedBy;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(
            Instant requestedAt
    ) {
        this.requestedAt = requestedAt;
    }

    public String getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(
            String decidedBy
    ) {
        this.decidedBy = decidedBy;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(
            Instant decidedAt
    ) {
        this.decidedAt = decidedAt;
    }

    public String getDecisionComment() {
        return decisionComment;
    }

    public void setDecisionComment(
            String decisionComment
    ) {
        this.decisionComment = decisionComment;
    }
}
