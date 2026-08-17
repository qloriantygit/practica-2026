package ru.practica2026.admin.outbox.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.outbox.entity.OutboxEvent;
import ru.practica2026.admin.outbox.entity.OutboxStatus;
import ru.practica2026.admin.outbox.event.AdminEventType;
import ru.practica2026.admin.outbox.repository.OutboxEventRepository;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OutboxEventService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxEventService(
            OutboxEventRepository repository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void enqueue(
            AdminEventType eventType,
            String correlationId,
            String actor,
            String entityId,
            String method,
            String path,
            String requestPayload,
            String responsePayload
    ) {
        try {
            UUID eventId =
                    UUID.randomUUID();

            Instant occurredAt =
                    Instant.now();

            Map<String, Object> payload =
                    new LinkedHashMap<>();

            payload.put(
                    "method",
                    method
            );

            payload.put(
                    "path",
                    path
            );

            payload.put(
                    "request",
                    requestPayload
            );

            payload.put(
                    "response",
                    responsePayload
            );

            Map<String, Object> envelope =
                    new LinkedHashMap<>();

            envelope.put(
                    "eventId",
                    eventId
            );

            envelope.put(
                    "eventType",
                    eventType.getEventName()
            );

            envelope.put(
                    "eventVersion",
                    "1.0"
            );

            envelope.put(
                    "correlationId",
                    correlationId
            );

            envelope.put(
                    "occurredAt",
                    occurredAt
            );

            envelope.put(
                    "source",
                    "admin-service"
            );

            envelope.put(
                    "actorId",
                    actor
            );

            envelope.put(
                    "entityId",
                    entityId
            );

            envelope.put(
                    "payload",
                    payload
            );

            OutboxEvent event =
                    new OutboxEvent();

            event.setEventId(eventId);

            event.setEventType(
                    eventType.getEventName()
            );

            event.setEventVersion("1.0");

            event.setCorrelationId(
                    correlationId
            );

            event.setSource(
                    "admin-service"
            );

            event.setActorId(actor);
            event.setEntityId(entityId);

            event.setRoutingKey(
                    eventType.getRoutingKey()
            );

            event.setPayload(
                    objectMapper
                            .writeValueAsString(
                                    envelope
                            )
            );

            event.setStatus(
                    OutboxStatus.PENDING
            );

            event.setRetryCount(0);

            event.setNextAttemptAt(
                    Instant.now()
            );

            event.setCreatedBy(actor);
            event.setUpdatedBy(actor);

            repository.saveAndFlush(event);
        }
        catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to create outbox event",
                    exception
            );
        }
    }
}
