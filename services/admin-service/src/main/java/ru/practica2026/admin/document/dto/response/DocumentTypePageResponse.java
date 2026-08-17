package ru.practica2026.admin.document.dto.response;

import java.util.List;

public record DocumentTypePageResponse(

        List<DocumentTypeResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages
) {
}
