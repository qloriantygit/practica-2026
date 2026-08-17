package ru.practica2026.admin.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import ru.practica2026.admin.common.entity.BaseEntity;

@Entity
@Table(name = "admin_audit_logs")
public class AdminAuditLog extends BaseEntity {

    @Column(
            name = "correlation_id",
            nullable = false,
            length = 100
    )
    private String correlationId;

    @Column(
            name = "actor",
            nullable = false,
            length = 255
    )
    private String actor;

    @Column(
            name = "http_method",
            nullable = false,
            length = 16
    )
    private String httpMethod;

    @Column(
            name = "request_path",
            nullable = false,
            length = 500
    )
    private String requestPath;

    @Column(
            name = "action",
            nullable = false,
            length = 600
    )
    private String action;

    @Column(
            name = "entity_type",
            length = 100
    )
    private String entityType;

    @Column(
            name = "entity_key",
            length = 100
    )
    private String entityKey;

    @Column(name = "before_state")
    private String beforeState;

    @Column(name = "after_state")
    private String afterState;

    @Column(
            name = "success",
            nullable = false
    )
    private boolean success;

    @Column(name = "error_message")
    private String errorMessage;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(
            String correlationId
    ) {
        this.correlationId = correlationId;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(
            String actor
    ) {
        this.actor = actor;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(
            String httpMethod
    ) {
        this.httpMethod = httpMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(
            String requestPath
    ) {
        this.requestPath = requestPath;
    }

    public String getAction() {
        return action;
    }

    public void setAction(
            String action
    ) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(
            String entityType
    ) {
        this.entityType = entityType;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(
            String entityKey
    ) {
        this.entityKey = entityKey;
    }

    public String getBeforeState() {
        return beforeState;
    }

    public void setBeforeState(
            String beforeState
    ) {
        this.beforeState = beforeState;
    }

    public String getAfterState() {
        return afterState;
    }

    public void setAfterState(
            String afterState
    ) {
        this.afterState = afterState;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(
            boolean success
    ) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(
            String errorMessage
    ) {
        this.errorMessage = errorMessage;
    }
}
