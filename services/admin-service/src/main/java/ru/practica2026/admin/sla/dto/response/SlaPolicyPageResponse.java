package ru.practica2026.admin.sla.dto.response;

import java.util.List;

public record SlaPolicyPageResponse(

        List<SlaPolicyResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages
) {
}
