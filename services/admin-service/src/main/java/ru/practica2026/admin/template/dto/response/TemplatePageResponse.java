package ru.practica2026.admin.template.dto.response;

import java.util.List;

public record TemplatePageResponse(

        List<TemplateResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages
) {
}
