package ru.practica2026.admin.outbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import ru.practica2026.admin.common.entity.BaseEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent extends BaseEntity {

    @Column(
            name = "event_id",
            nullable = false,
            unique = true
    )
    private UUID eventId;

    @Column(
            name = "event_type",
            nullable = false,
            length = 100
    )
    private String eventType;

    @Column(
            name = "event_version",
            nullable = false,
            length = 20
    )
    private String eventVersion;

    @Column(
            name = "correlation_id",
            nullable = false,
            length = 100
    )
    private String correlationId;

    @Column(
            name = "source",
            nullable = false,
            length = 100
    )
    private String source;

    @Column(
            name = "actor_id",
            length = 255
    )
    private String actorId;

    @Column(
            name = "entity_id",
            length = 100
    )
    private String entityId;

    @Column(
            name = "routing_key",
            nullable = false,
            length = 150
    )
    private String routingKey;

    @Column(
            name = "payload",
            nullable = false
    )
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 32
    )
    private OutboxStatus status;

    @Column(
            name = "retry_count",
            nullable = false
    )
    private int retryCount;

    @Column(
            name = "next_attempt_at",
            nullable = false
    )
    private Instant nextAttemptAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "last_error")
    private String lastError;

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(
            String eventType
    ) {
        this.eventType = eventType;
    }

    public String getEventVersion() {
        return eventVersion;
    }

    public void setEventVersion(
            String eventVersion
    ) {
        this.eventVersion = eventVersion;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(
            String correlationId
    ) {
        this.correlationId = correlationId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(
            String source
    ) {
        this.source = source;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(
            String actorId
    ) {
        this.actorId = actorId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(
            String entityId
    ) {
        this.entityId = entityId;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(
            String routingKey
    ) {
        this.routingKey = routingKey;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(
            String payload
    ) {
        this.payload = payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public void setStatus(
            OutboxStatus status
    ) {
        this.status = status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(
            int retryCount
    ) {
        this.retryCount = retryCount;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }

    public void setNextAttemptAt(
            Instant nextAttemptAt
    ) {
        this.nextAttemptAt = nextAttemptAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(
            Instant sentAt
    ) {
        this.sentAt = sentAt;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(
            String lastError
    ) {
        this.lastError = lastError;
    }
}
