package ru.practica2026.notification.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.Message;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Component;

import ru.practica2026.notification.config.NotificationRabbitConfig;
import ru.practica2026.notification.event.NotificationRequestedPayload;
import ru.practica2026.notification.service.NotificationDeliveryService;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class NotificationEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(
                    NotificationEventConsumer.class
            );

    private static final String EVENT_TYPE =
            "NotificationRequested";

    private static final String SUPPORTED_VERSION =
            "1.0";

    private final ObjectMapper objectMapper;
    private final NotificationDeliveryService deliveryService;

    public NotificationEventConsumer(
            ObjectMapper objectMapper,
            NotificationDeliveryService deliveryService
    ) {
        this.objectMapper = objectMapper;
        this.deliveryService = deliveryService;
    }

    @RabbitListener(
            queues = NotificationRabbitConfig.REQUEST_QUEUE
    )
    public void consume(
            Message message
    ) {
        try {
            String body =
                    new String(
                            message.getBody(),
                            StandardCharsets.UTF_8
                    );

            JsonNode envelope =
                    objectMapper.readTree(body);

            UUID eventId =
                    UUID.fromString(
                            requiredText(
                                    envelope,
                                    "eventId"
                            )
                    );

            String eventType =
                    requiredText(
                            envelope,
                            "eventType"
                    );

            String eventVersion =
                    requiredText(
                            envelope,
                            "eventVersion"
                    );

            String correlationId =
                    requiredText(
                            envelope,
                            "correlationId"
                    );

            if (!EVENT_TYPE.equals(eventType)) {
                throw new IllegalArgumentException(
                        "Unsupported eventType: "
                                + eventType
                );
            }

            if (
                    !SUPPORTED_VERSION.equals(
                            eventVersion
                    )
            ) {
                throw new IllegalArgumentException(
                        "Unsupported NotificationRequested eventVersion: "
                                + eventVersion
                );
            }

            JsonNode payloadNode =
                    envelope.get("payload");

            if (
                    payloadNode == null
                    ||
                    payloadNode.isNull()
            ) {
                throw new IllegalArgumentException(
                        "NotificationRequested payload is required"
                );
            }

            NotificationRequestedPayload payload =
                    objectMapper.treeToValue(
                            payloadNode,
                            NotificationRequestedPayload.class
                    );

            log.info(
                    "NotificationRequested received eventId={} correlationId={}",
                    eventId,
                    correlationId
            );

            deliveryService.process(
                    eventId,
                    correlationId,
                    eventVersion,
                    payload
            );
        }
        catch (Exception exception) {
            log.error(
                    "NotificationRequested processing failed",
                    exception
            );

            throw new IllegalStateException(
                    "Notification event processing failed",
                    exception
            );
        }
    }

    private String requiredText(
            JsonNode node,
            String field
    ) {
        JsonNode value =
                node.get(field);

        if (
                value == null
                ||
                value.isNull()
                ||
                value.asText().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Required event field is missing: "
                            + field
            );
        }

        return value.asText();
    }
}
