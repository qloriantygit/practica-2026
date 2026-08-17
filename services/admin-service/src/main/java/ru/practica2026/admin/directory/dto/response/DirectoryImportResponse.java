package ru.practica2026.admin.directory.dto.response;

import ru.practica2026.admin.directory.file.DirectoryFileFormat;

import java.util.UUID;

public record DirectoryImportResponse(

        UUID versionBusinessKey,

        DirectoryFileFormat format,

        int importedItems,

        String storageObject,

        String importedBy
) {
}
