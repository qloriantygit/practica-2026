package ru.practica2026.admin.outbox.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.data.domain.PageRequest;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.outbox.config.RabbitInfrastructureConfig;
import ru.practica2026.admin.outbox.entity.OutboxEvent;
import ru.practica2026.admin.outbox.entity.OutboxStatus;
import ru.practica2026.admin.outbox.repository.OutboxEventRepository;

import java.nio.charset.StandardCharsets;

import java.time.Instant;

import java.util.List;

@Service
public class OutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(
                    OutboxPublisher.class
            );

    private static final int MAX_RETRIES = 5;

    private final OutboxEventRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public OutboxPublisher(
            OutboxEventRepository repository,
            RabbitTemplate rabbitTemplate
    ) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publishReadyEvents() {

        List<OutboxEvent> events =
                repository.findReady(
                        List.of(
                                OutboxStatus.PENDING,
                                OutboxStatus.FAILED
                        ),
                        Instant.now(),
                        PageRequest.of(0, 20)
                );

        for (OutboxEvent event : events) {
            publish(event);
        }
    }

    private void publish(
            OutboxEvent event
    ) {
        try {
            MessageProperties properties =
                    new MessageProperties();

            properties.setContentType(
                    MessageProperties
                            .CONTENT_TYPE_JSON
            );

            properties.setContentEncoding(
                    StandardCharsets.UTF_8.name()
            );

            properties.setCorrelationId(
                    event.getCorrelationId()
            );

            properties.setMessageId(
                    event.getEventId()
                            .toString()
            );

            properties.setHeader(
                    "eventType",
                    event.getEventType()
            );

            properties.setHeader(
                    "eventVersion",
                    event.getEventVersion()
            );

            Message message =
                    new Message(
                            event.getPayload()
                                    .getBytes(
                                            StandardCharsets.UTF_8
                                    ),
                            properties
                    );

            rabbitTemplate.send(
                                resolveExchange(event),
                                event.getRoutingKey(),
                                message
                        );

            event.setStatus(
                    OutboxStatus.SENT
            );

            event.setSentAt(
                    Instant.now()
            );

            event.setLastError(null);

            log.info(
                    "Outbox event sent eventId={} type={} correlationId={}",
                    event.getEventId(),
                    event.getEventType(),
                    event.getCorrelationId()
            );
        }
        catch (Exception exception) {

            int retryCount =
                    event.getRetryCount() + 1;

            event.setRetryCount(
                    retryCount
            );

            event.setLastError(
                    limitError(
                            exception.getMessage()
                    )
            );

            if (retryCount >= MAX_RETRIES) {

                event.setStatus(
                        OutboxStatus.DEAD
                );

                log.error(
                        "Outbox event moved to DEAD eventId={} retries={}",
                        event.getEventId(),
                        retryCount,
                        exception
                );
            }
            else {
                event.setStatus(
                        OutboxStatus.FAILED
                );

                long delaySeconds =
                        Math.min(
                                60,
                                1L << retryCount
                        );

                event.setNextAttemptAt(
                        Instant.now()
                                .plusSeconds(
                                        delaySeconds
                                )
                );

                log.warn(
                        "Outbox publish failed eventId={} retry={} nextAttemptIn={}s",
                        event.getEventId(),
                        retryCount,
                        delaySeconds
                );
            }
        }
    }

    private String limitError(
            String message
    ) {
        if (message == null) {
            return "Unknown RabbitMQ error";
        }

        if (message.length() <= 4000) {
            return message;
        }

        return message.substring(
                0,
                4000
        );
    }

    private String resolveExchange(
            OutboxEvent event
    ) {
        if (
                "NotificationRequested"
                        .equals(event.getEventType())
        ) {
            return RabbitInfrastructureConfig
                    .NOTIFICATION_EXCHANGE;
        }

        return RabbitInfrastructureConfig.EXCHANGE;
    }
}
