package ru.practica2026.notification.event;

import ru.practica2026.notification.entity.NotificationChannel;

public record NotificationRequestedPayload(

        NotificationChannel channel,

        String recipient,

        String subject,

        String body
) {
}
