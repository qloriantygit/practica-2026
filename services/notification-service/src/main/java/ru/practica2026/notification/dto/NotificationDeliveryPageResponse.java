package ru.practica2026.notification.dto;

import java.util.List;

public record NotificationDeliveryPageResponse(

        List<NotificationDeliveryResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages
) {
}
