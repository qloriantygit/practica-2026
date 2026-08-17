package ru.practica2026.admin.directory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;
import ru.practica2026.admin.directory.dto.request.CreateDirectoryVersionRequest;
import ru.practica2026.admin.directory.dto.response.DirectoryVersionResponse;
import ru.practica2026.admin.directory.entity.Directory;
import ru.practica2026.admin.directory.entity.DirectoryItem;
import ru.practica2026.admin.directory.entity.DirectoryVersion;
import ru.practica2026.admin.directory.entity.DirectoryVersionStatus;
import ru.practica2026.admin.directory.mapper.DirectoryMapper;
import ru.practica2026.admin.directory.repository.DirectoryItemRepository;
import ru.practica2026.admin.directory.repository.DirectoryRepository;
import ru.practica2026.admin.directory.repository.DirectoryVersionRepository;
import ru.practica2026.admin.security.service.CurrentActorService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DirectoryLifecycleService {

    private final DirectoryRepository directoryRepository;
    private final DirectoryVersionRepository directoryVersionRepository;
    private final DirectoryItemRepository directoryItemRepository;
    private final CurrentActorService currentActorService;

    public DirectoryLifecycleService(
            DirectoryRepository directoryRepository,
            DirectoryVersionRepository directoryVersionRepository,
            DirectoryItemRepository directoryItemRepository,
            CurrentActorService currentActorService
    ) {
        this.directoryRepository = directoryRepository;
        this.directoryVersionRepository = directoryVersionRepository;
        this.directoryItemRepository = directoryItemRepository;
        this.currentActorService = currentActorService;
    }

    @Transactional
    public DirectoryVersionResponse submit(
            UUID versionBusinessKey
    ) {
        DirectoryVersion version =
                getVersion(versionBusinessKey);

        if (
                version.getStatus()
                        != DirectoryVersionStatus.DRAFT
        ) {
            throw new ConflictException(
                    "Only DRAFT directory version can be submitted for approval"
            );
        }

        long itemCount =
                directoryItemRepository
                        .countByDirectoryVersion(version);

        if (itemCount == 0) {
            throw new ConflictException(
                    "Empty directory version cannot be submitted for approval"
            );
        }

        version.setStatus(
                DirectoryVersionStatus.ON_APPROVAL
        );

        version.setUpdatedBy(
                currentActorService.getCurrentActor()
        );

        directoryVersionRepository.flush();

        return DirectoryMapper.toVersion(
                version,
                itemCount
        );
    }

    @Transactional
    public DirectoryVersionResponse publish(
            UUID versionBusinessKey
    ) {
        DirectoryVersion version =
                getVersion(versionBusinessKey);

        if (
                version.getStatus()
                        != DirectoryVersionStatus.ON_APPROVAL
        ) {
            throw new ConflictException(
                    "Only ON_APPROVAL directory version can be published"
            );
        }

        long itemCount =
                directoryItemRepository
                        .countByDirectoryVersion(version);

        if (itemCount == 0) {
            throw new ConflictException(
                    "Empty directory version cannot be published"
            );
        }

        String actor =
                currentActorService.getCurrentActor();

        List<DirectoryVersion> publishedVersions =
                directoryVersionRepository
                        .findAllByDirectoryAndStatus(
                                version.getDirectory(),
                                DirectoryVersionStatus.PUBLISHED
                        );

        for (
                DirectoryVersion publishedVersion
                : publishedVersions
        ) {
            if (
                    !publishedVersion.getId()
                            .equals(version.getId())
            ) {
                publishedVersion.setStatus(
                        DirectoryVersionStatus.ARCHIVED
                );

                publishedVersion.setUpdatedBy(actor);
            }
        }

        version.setStatus(
                DirectoryVersionStatus.PUBLISHED
        );

        version.setUpdatedBy(actor);

        directoryVersionRepository.flush();

        return DirectoryMapper.toVersion(
                version,
                itemCount
        );
    }

    @Transactional
    public DirectoryVersionResponse createNextVersion(
            UUID directoryBusinessKey,
            CreateDirectoryVersionRequest request
    ) {
        Directory directory =
                getDirectory(directoryBusinessKey);

        if (
                directoryVersionRepository
                        .existsByDirectoryAndStatus(
                                directory,
                                DirectoryVersionStatus.DRAFT
                        )
        ) {
            throw new ConflictException(
                    "Directory already has a DRAFT version"
            );
        }

        if (
                directoryVersionRepository
                        .existsByDirectoryAndStatus(
                                directory,
                                DirectoryVersionStatus.ON_APPROVAL
                        )
        ) {
            throw new ConflictException(
                    "Directory already has a version ON_APPROVAL"
            );
        }

        DirectoryVersion sourceVersion =
                directoryVersionRepository
                        .findTopByDirectoryAndStatusOrderByVersionNumberDesc(
                                directory,
                                DirectoryVersionStatus.PUBLISHED
                        )
                        .orElseThrow(
                                () ->
                                        new ConflictException(
                                                "New version can be created only from a PUBLISHED version"
                                        )
                        );

        DirectoryVersion latestVersion =
                directoryVersionRepository
                        .findTopByDirectoryOrderByVersionNumberDesc(
                                directory
                        )
                        .orElse(sourceVersion);

        LocalDate validFrom =
                request.validFrom() != null
                        ? request.validFrom()
                        : sourceVersion.getValidFrom();

        LocalDate validTo =
                request.validTo() != null
                        ? request.validTo()
                        : sourceVersion.getValidTo();

        validateValidity(
                validFrom,
                validTo
        );

        String actor =
                currentActorService.getCurrentActor();

        DirectoryVersion newVersion =
                new DirectoryVersion();

        newVersion.setDirectory(directory);

        newVersion.setVersionNumber(
                latestVersion.getVersionNumber() + 1
        );

        newVersion.setStatus(
                DirectoryVersionStatus.DRAFT
        );

        newVersion.setValidFrom(validFrom);
        newVersion.setValidTo(validTo);

        newVersion.setCreatedBy(actor);
        newVersion.setUpdatedBy(actor);

        DirectoryVersion savedVersion =
                directoryVersionRepository
                        .saveAndFlush(newVersion);

        List<DirectoryItem> sourceItems =
                directoryItemRepository
                        .findAllByDirectoryVersionOrderBySortOrderAscCodeAsc(
                                sourceVersion
                        );

        List<DirectoryItem> copiedItems =
                new ArrayList<>();

        for (DirectoryItem sourceItem : sourceItems) {

            DirectoryItem copiedItem =
                    new DirectoryItem();

            copiedItem.setDirectoryVersion(
                    savedVersion
            );

            copiedItem.setCode(
                    sourceItem.getCode()
            );

            copiedItem.setName(
                    sourceItem.getName()
            );

            copiedItem.setDescription(
                    sourceItem.getDescription()
            );

            copiedItem.setEnabled(
                    sourceItem.isEnabled()
            );

            copiedItem.setSortOrder(
                    sourceItem.getSortOrder()
            );

            copiedItem.setAttributes(
                    sourceItem.getAttributes()
            );

            copiedItem.setCreatedBy(actor);
            copiedItem.setUpdatedBy(actor);

            copiedItems.add(copiedItem);
        }

        if (!copiedItems.isEmpty()) {
            directoryItemRepository
                    .saveAllAndFlush(
                            copiedItems
                    );
        }

        return DirectoryMapper.toVersion(
                savedVersion,
                copiedItems.size()
        );
    }

    private Directory getDirectory(
            UUID businessKey
    ) {
        return directoryRepository
                .findByBusinessKey(businessKey)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Directory not found: "
                                                + businessKey
                                )
                );
    }

    private DirectoryVersion getVersion(
            UUID businessKey
    ) {
        return directoryVersionRepository
                .findByBusinessKey(businessKey)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Directory version not found: "
                                                + businessKey
                                )
                );
    }

    private void validateValidity(
            LocalDate validFrom,
            LocalDate validTo
    ) {
        if (
                validFrom != null
                &&
                validTo != null
                &&
                validTo.isBefore(validFrom)
        ) {
            throw new ConflictException(
                    "validTo cannot be earlier than validFrom"
            );
        }
    }
}
