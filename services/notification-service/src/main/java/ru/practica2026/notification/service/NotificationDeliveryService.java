package ru.practica2026.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;

import ru.practica2026.notification.entity.NotificationChannel;
import ru.practica2026.notification.entity.NotificationDelivery;
import ru.practica2026.notification.entity.NotificationStatus;
import ru.practica2026.notification.event.NotificationRequestedPayload;
import ru.practica2026.notification.repository.NotificationDeliveryRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class NotificationDeliveryService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    NotificationDeliveryService.class
            );

    private final NotificationDeliveryRepository repository;
    private final JavaMailSender mailSender;
    private final String mailFrom;

    public NotificationDeliveryService(
            NotificationDeliveryRepository repository,
            JavaMailSender mailSender,

            @Value(
                    "${notification.mail.from:no-reply@practica.local}"
            )
            String mailFrom
    ) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
    }

    public void process(
            UUID eventId,
            String correlationId,
            String eventVersion,
            NotificationRequestedPayload payload
    ) {
        validatePayload(payload);

        NotificationDelivery delivery =
                repository.findByEventId(eventId)
                        .orElseGet(
                                () -> createDelivery(
                                        eventId,
                                        correlationId,
                                        eventVersion,
                                        payload
                                )
                        );

        /*
         * Idempotency:
         * RabbitMQ may redeliver the same event.
         * An already successfully delivered event
         * must never send the email twice.
         */
        if (
                delivery.getStatus()
                        == NotificationStatus.SENT
        ) {
            log.info(
                    "Duplicate notification ignored eventId={} correlationId={}",
                    eventId,
                    correlationId
            );

            return;
        }

        delivery.setAttemptCount(
                delivery.getAttemptCount() + 1
        );

        delivery.setStatus(
                NotificationStatus.RECEIVED
        );

        delivery.setLastError(null);

        repository.saveAndFlush(delivery);

        try {
            deliver(payload);

            delivery.setStatus(
                    NotificationStatus.SENT
            );

            delivery.setSentAt(
                    Instant.now()
            );

            delivery.setLastError(null);

            repository.saveAndFlush(delivery);

            log.info(
                    "Notification sent eventId={} channel={} recipient={} correlationId={}",
                    eventId,
                    payload.channel(),
                    payload.recipient(),
                    correlationId
            );
        }
        catch (Exception exception) {

            delivery.setStatus(
                    NotificationStatus.FAILED
            );

            delivery.setLastError(
                    limitError(
                            exception.getMessage()
                    )
            );

            repository.saveAndFlush(delivery);

            log.error(
                    "Notification delivery failed eventId={} attempt={} correlationId={}",
                    eventId,
                    delivery.getAttemptCount(),
                    correlationId,
                    exception
            );

            throw new IllegalStateException(
                    "Notification delivery failed",
                    exception
            );
        }
    }

    private NotificationDelivery createDelivery(
            UUID eventId,
            String correlationId,
            String eventVersion,
            NotificationRequestedPayload payload
    ) {
        NotificationDelivery delivery =
                new NotificationDelivery();

        delivery.setEventId(eventId);

        delivery.setCorrelationId(
                correlationId
        );

        delivery.setEventVersion(
                eventVersion
        );

        delivery.setChannel(
                payload.channel()
        );

        delivery.setRecipient(
                payload.recipient().trim()
        );

        delivery.setSubject(
                normalizeNullable(
                        payload.subject()
                )
        );

        delivery.setBody(
                payload.body()
        );

        delivery.setStatus(
                NotificationStatus.RECEIVED
        );

        delivery.setAttemptCount(0);

        return repository
                .saveAndFlush(delivery);
    }

    private void deliver(
            NotificationRequestedPayload payload
    ) {
        if (
                payload.channel()
                        != NotificationChannel.EMAIL
        ) {
            throw new IllegalArgumentException(
                    "Channel is not implemented in local notification-service: "
                            + payload.channel()
            );
        }

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(mailFrom);

        message.setTo(
                payload.recipient().trim()
        );

        message.setSubject(
                normalizeNullable(
                        payload.subject()
                )
        );

        message.setText(
                payload.body()
        );

        mailSender.send(message);
    }

    private void validatePayload(
            NotificationRequestedPayload payload
    ) {
        if (payload == null) {
            throw new IllegalArgumentException(
                    "Notification payload is required"
            );
        }

        if (payload.channel() == null) {
            throw new IllegalArgumentException(
                    "Notification channel is required"
            );
        }

        if (
                payload.recipient() == null
                ||
                payload.recipient().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Notification recipient is required"
            );
        }

        if (
                payload.body() == null
                ||
                payload.body().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Notification body is required"
            );
        }
    }

    private String normalizeNullable(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }

    private String limitError(
            String value
    ) {
        if (value == null) {
            return "Unknown notification error";
        }

        return value.length() <= 4000
                ? value
                : value.substring(0, 4000);
    }
}
