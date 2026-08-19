package ru.practica2026.notification.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import ru.practica2026.notification.event.NotificationRequestedPayload;
import ru.practica2026.notification.service.NotificationDeliveryService;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NotificationEventConsumerTest {

    @Mock
    private NotificationDeliveryService deliveryService;

    @Test
    void validNotificationRequestedEventIsProcessed() {
        ObjectMapper objectMapper =
                new ObjectMapper();

        NotificationEventConsumer consumer =
                new NotificationEventConsumer(
                        objectMapper,
                        deliveryService
                );

        UUID eventId =
                UUID.randomUUID();

        String correlationId =
                "notification-test-correlation";

        String json =
                """
                {
                  "eventId": "%s",
                  "eventType": "NotificationRequested",
                  "eventVersion": "1.0",
                  "correlationId": "%s",
                  "occurredAt": "2026-08-20T00:00:00Z",
                  "source": "admin-service",
                  "actorId": "test.admin",
                  "entityId": "approval-test",
                  "payload": {
                    "channel": "EMAIL",
                    "recipient": "student@example.local",
                    "subject": "Test notification",
                    "body": "Notification body"
                  }
                }
                """.formatted(
                        eventId,
                        correlationId
                );

        Message message =
                new Message(
                        json.getBytes(
                                StandardCharsets.UTF_8
                        ),
                        new MessageProperties()
                );

        consumer.consume(message);

        ArgumentCaptor<NotificationRequestedPayload> payloadCaptor =
                ArgumentCaptor.forClass(
                        NotificationRequestedPayload.class
                );

        verify(deliveryService)
                .process(
                        eq(eventId),
                        eq(correlationId),
                        eq("1.0"),
                        payloadCaptor.capture()
                );

        assertNotNull(
                payloadCaptor.getValue()
        );
    }

    @Test
    void unsupportedEventTypeIsRejected() {
        ObjectMapper objectMapper =
                new ObjectMapper();

        NotificationEventConsumer consumer =
                new NotificationEventConsumer(
                        objectMapper,
                        deliveryService
                );

        String json =
                """
                {
                  "eventId": "%s",
                  "eventType": "UnknownEvent",
                  "eventVersion": "1.0",
                  "correlationId": "invalid-event-test",
                  "payload": {
                    "channel": "EMAIL",
                    "recipient": "student@example.local",
                    "subject": "Invalid",
                    "body": "Invalid"
                  }
                }
                """.formatted(
                        UUID.randomUUID()
                );

        Message message =
                new Message(
                        json.getBytes(
                                StandardCharsets.UTF_8
                        ),
                        new MessageProperties()
                );

        assertThrows(
                IllegalStateException.class,
                () -> consumer.consume(message)
        );

        verifyNoInteractions(
                deliveryService
        );
    }
}
