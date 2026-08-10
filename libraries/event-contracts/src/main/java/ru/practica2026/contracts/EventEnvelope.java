package ru.practica2026.contracts;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventEnvelope<T>(

        @NotNull
        UUID eventId,

        @NotNull
        EventType eventType,

        @NotBlank
        String eventVersion,

        @NotNull
        UUID correlationId,

        @NotNull
        Instant occurredAt,

        @NotBlank
        String source,

        String actorId,

        @NotBlank
        String entityId,

        @NotNull
        T payload
) {

    public static <T> EventEnvelope<T> create(

            EventType eventType,

            String source,

            String actorId,

            String entityId,

            UUID correlationId,

            T payload
    ) {

        return new EventEnvelope<>(

                UUID.randomUUID(),

                eventType,

                "1.0",

                correlationId,

                Instant.now(),

                source,

                actorId,

                entityId,

                payload
        );
    }
}
