package ru.practica2026.admin.directory.mapper;

import ru.practica2026.admin.directory.dto.response.DirectoryDetailResponse;
import ru.practica2026.admin.directory.dto.response.DirectoryItemResponse;
import ru.practica2026.admin.directory.dto.response.DirectorySummaryResponse;
import ru.practica2026.admin.directory.dto.response.DirectoryVersionResponse;
import ru.practica2026.admin.directory.entity.Directory;
import ru.practica2026.admin.directory.entity.DirectoryItem;
import ru.practica2026.admin.directory.entity.DirectoryVersion;

import java.util.LinkedHashMap;
import java.util.List;

public final class DirectoryMapper {

    private DirectoryMapper() {
    }

    public static DirectorySummaryResponse toSummary(
            Directory directory,
            DirectoryVersion latestVersion
    ) {
        return new DirectorySummaryResponse(
                directory.getBusinessKey(),
                directory.getCode(),
                directory.getName(),
                directory.getDescription(),
                latestVersion == null
                        ? null
                        : latestVersion.getVersionNumber(),
                latestVersion == null
                        ? null
                        : latestVersion.getStatus(),
                directory.getVersion(),
                directory.getCreatedAt(),
                directory.getUpdatedAt(),
                directory.getCreatedBy(),
                directory.getUpdatedBy()
        );
    }

    public static DirectoryDetailResponse toDetail(
            Directory directory,
            List<DirectoryVersionResponse> versions
    ) {
        return new DirectoryDetailResponse(
                directory.getBusinessKey(),
                directory.getCode(),
                directory.getName(),
                directory.getDescription(),
                directory.getVersion(),
                directory.getCreatedAt(),
                directory.getUpdatedAt(),
                directory.getCreatedBy(),
                directory.getUpdatedBy(),
                versions
        );
    }

    public static DirectoryVersionResponse toVersion(
            DirectoryVersion directoryVersion,
            long itemCount
    ) {
        return new DirectoryVersionResponse(
                directoryVersion.getBusinessKey(),
                directoryVersion.getVersionNumber(),
                directoryVersion.getStatus(),
                directoryVersion.getValidFrom(),
                directoryVersion.getValidTo(),
                itemCount,
                directoryVersion.getVersion(),
                directoryVersion.getCreatedAt(),
                directoryVersion.getUpdatedAt(),
                directoryVersion.getCreatedBy(),
                directoryVersion.getUpdatedBy()
        );
    }

    public static DirectoryItemResponse toItem(
            DirectoryItem item
    ) {
        return new DirectoryItemResponse(
                item.getBusinessKey(),
                item.getDirectoryVersion().getBusinessKey(),
                item.getCode(),
                item.getName(),
                item.getDescription(),
                item.isEnabled(),
                item.getSortOrder(),
                new LinkedHashMap<>(item.getAttributes()),
                item.getVersion(),
                item.getCreatedAt(),
                item.getUpdatedAt(),
                item.getCreatedBy(),
                item.getUpdatedBy()
        );
    }
}
