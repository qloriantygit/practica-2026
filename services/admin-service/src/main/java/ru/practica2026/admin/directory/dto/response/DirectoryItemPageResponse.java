package ru.practica2026.admin.directory.dto.response;

import java.util.List;

public record DirectoryItemPageResponse(

        List<DirectoryItemResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages
) {
}
