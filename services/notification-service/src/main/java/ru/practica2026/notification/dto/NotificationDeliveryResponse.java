package ru.practica2026.notification.dto;

import ru.practica2026.notification.entity.NotificationChannel;
import ru.practica2026.notification.entity.NotificationStatus;

import java.time.Instant;
import java.util.UUID;

public record NotificationDeliveryResponse(

        UUID businessKey,

        UUID eventId,

        String correlationId,

        String eventVersion,

        NotificationChannel channel,

        String recipient,

        String subject,

        NotificationStatus status,

        int attemptCount,

        Instant receivedAt,

        Instant sentAt,

        String lastError
) {
}
