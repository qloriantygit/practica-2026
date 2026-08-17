package ru.practica2026.admin.sla.dto.response;

import java.util.List;

public record WorkCalendarPageResponse(

        List<WorkCalendarResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages
) {
}
