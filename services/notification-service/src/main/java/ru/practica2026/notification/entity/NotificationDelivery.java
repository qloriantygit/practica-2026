package ru.practica2026.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_deliveries")
public class NotificationDelivery {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            name = "business_key",
            nullable = false,
            unique = true
    )
    private UUID businessKey;

    @Column(
            name = "event_id",
            nullable = false,
            unique = true
    )
    private UUID eventId;

    @Column(
            name = "correlation_id",
            nullable = false,
            length = 100
    )
    private String correlationId;

    @Column(
            name = "event_version",
            nullable = false,
            length = 20
    )
    private String eventVersion;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "channel",
            nullable = false,
            length = 32
    )
    private NotificationChannel channel;

    @Column(
            name = "recipient",
            nullable = false,
            length = 500
    )
    private String recipient;

    @Column(
            name = "subject",
            length = 1000
    )
    private String subject;

    @Column(
            name = "body",
            nullable = false
    )
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 32
    )
    private NotificationStatus status;

    @Column(
            name = "attempt_count",
            nullable = false
    )
    private int attemptCount;

    @Column(
            name = "received_at",
            nullable = false
    )
    private Instant receivedAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "last_error")
    private String lastError;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();

        if (businessKey == null) {
            businessKey = UUID.randomUUID();
        }

        if (receivedAt == null) {
            receivedAt = now;
        }

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public UUID getBusinessKey() {
        return businessKey;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(
            String correlationId
    ) {
        this.correlationId = correlationId;
    }

    public String getEventVersion() {
        return eventVersion;
    }

    public void setEventVersion(
            String eventVersion
    ) {
        this.eventVersion = eventVersion;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(
            NotificationChannel channel
    ) {
        this.channel = channel;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(
            String recipient
    ) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(
            String subject
    ) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(
            String body
    ) {
        this.body = body;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(
            NotificationStatus status
    ) {
        this.status = status;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(
            int attemptCount
    ) {
        this.attemptCount = attemptCount;
    }

    public Instant getReceivedAt() {
        return receivedAt;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
