package ru.practica2026.admin.directory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;
import ru.practica2026.admin.directory.dto.request.CreateDirectoryItemRequest;
import ru.practica2026.admin.directory.dto.request.CreateDirectoryRequest;
import ru.practica2026.admin.directory.dto.request.UpdateDirectoryItemRequest;
import ru.practica2026.admin.directory.dto.request.UpdateDirectoryRequest;
import ru.practica2026.admin.directory.dto.response.DirectoryDetailResponse;
import ru.practica2026.admin.directory.dto.response.DirectoryItemPageResponse;
import ru.practica2026.admin.directory.dto.response.DirectoryItemResponse;
import ru.practica2026.admin.directory.dto.response.DirectoryPageResponse;
import ru.practica2026.admin.directory.dto.response.DirectorySummaryResponse;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class DirectoryService {

    private static final Set<String> DIRECTORY_SORT_FIELDS =
            Set.of(
                    "code",
                    "name",
                    "createdAt",
                    "updatedAt"
            );

    private static final Set<String> ITEM_SORT_FIELDS =
            Set.of(
                    "sortOrder",
                    "code",
                    "name",
                    "createdAt",
                    "updatedAt"
            );

    private final DirectoryRepository directoryRepository;
    private final DirectoryVersionRepository directoryVersionRepository;
    private final DirectoryItemRepository directoryItemRepository;
    private final CurrentActorService currentActorService;

    public DirectoryService(
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
    public DirectoryDetailResponse create(
            CreateDirectoryRequest request
    ) {
    String code = normalizeCode(request.code());

    if (directoryRepository.existsByCodeIgnoreCase(code)) {
        throw new ConflictException(
                "Directory with code '" + code + "' already exists"
        );
    }

    validateValidity(
            request.validFrom(),
            request.validTo()
    );

    String actor =
            currentActorService.getCurrentActor();

    Directory directory = new Directory();

    directory.setCode(code);
    directory.setName(request.name().trim());
    directory.setDescription(
            normalizeNullable(request.description())
    );
    directory.setCreatedBy(actor);
    directory.setUpdatedBy(actor);

    Directory savedDirectory =
            directoryRepository.saveAndFlush(
                    directory
            );

    DirectoryVersion initialVersion =
            new DirectoryVersion();

    initialVersion.setDirectory(
            savedDirectory
    );
    initialVersion.setVersionNumber(1);
    initialVersion.setStatus(
            DirectoryVersionStatus.DRAFT
    );
    initialVersion.setValidFrom(
            request.validFrom()
    );
    initialVersion.setValidTo(
            request.validTo()
    );
    initialVersion.setCreatedBy(actor);
    initialVersion.setUpdatedBy(actor);

    directoryVersionRepository
            .saveAndFlush(initialVersion);

    return buildDetailResponse(
            savedDirectory
    );
}

    @Transactional(readOnly = true)
    public DirectoryPageResponse findAll(
            String search,
            DirectoryVersionStatus status,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        int safePage = Math.max(page, 0);
        int safeSize =
                Math.min(
                        Math.max(size, 1),
                        100
                );

        String safeSortBy =
                DIRECTORY_SORT_FIELDS.contains(sortBy)
                        ? sortBy
                        : "code";

        Sort.Direction sortDirection =
                "DESC".equalsIgnoreCase(direction)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        PageRequest pageRequest =
                PageRequest.of(
                        safePage,
                        safeSize,
                        Sort.by(
                                sortDirection,
                                safeSortBy
                        )
                );

        Page<Directory> result =
                directoryRepository.search(
                        normalizeSearch(search),
                        status,
                        pageRequest
                );

        List<DirectorySummaryResponse> content =
                result.getContent()
                        .stream()
                        .map(directory -> {
                            DirectoryVersion latest =
                                    directoryVersionRepository
                                            .findTopByDirectoryOrderByVersionNumberDesc(
                                                    directory
                                            )
                                            .orElse(null);

                            return DirectoryMapper.toSummary(
                                    directory,
                                    latest
                            );
                        })
                        .toList();

        return new DirectoryPageResponse(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public DirectoryDetailResponse findByBusinessKey(
            UUID businessKey
    ) {
        return buildDetailResponse(
                getDirectory(businessKey)
        );
    }

    @Transactional
    public DirectoryDetailResponse update(
            UUID businessKey,
            UpdateDirectoryRequest request
    ) {
        Directory directory =
                getDirectory(businessKey);

        directory.setName(
                request.name().trim()
        );

        directory.setDescription(
                normalizeNullable(
                        request.description()
                )
        );

        directory.setUpdatedBy(
                currentActorService.getCurrentActor()
        );

        directoryRepository.flush();

        return buildDetailResponse(directory);
    }

    @Transactional(readOnly = true)
    public List<DirectoryVersionResponse> findVersions(
            UUID directoryBusinessKey
    ) {
        Directory directory =
                getDirectory(directoryBusinessKey);

        return directoryVersionRepository
                .findAllByDirectoryOrderByVersionNumberDesc(
                        directory
                )
                .stream()
                .map(this::buildVersionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DirectoryVersionResponse findVersion(
            UUID versionBusinessKey
    ) {
        return buildVersionResponse(
                getDirectoryVersion(
                        versionBusinessKey
                )
        );
    }

    @Transactional(readOnly = true)
    public DirectoryItemPageResponse findItems(
            UUID versionBusinessKey,
            String search,
            Boolean enabled,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        DirectoryVersion directoryVersion =
                getDirectoryVersion(
                        versionBusinessKey
                );

        int safePage = Math.max(page, 0);
        int safeSize =
                Math.min(
                        Math.max(size, 1),
                        100
                );

        String safeSortBy =
                ITEM_SORT_FIELDS.contains(sortBy)
                        ? sortBy
                        : "sortOrder";

        Sort.Direction sortDirection =
                "DESC".equalsIgnoreCase(direction)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        PageRequest pageRequest =
                PageRequest.of(
                        safePage,
                        safeSize,
                        Sort.by(
                                sortDirection,
                                safeSortBy
                        )
                );

        Page<DirectoryItem> result =
                directoryItemRepository.search(
                        directoryVersion,
                        normalizeSearch(search),
                        enabled,
                        pageRequest
                );

        return new DirectoryItemPageResponse(
                result.getContent()
                        .stream()
                        .map(DirectoryMapper::toItem)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional
    public DirectoryItemResponse createItem(
            UUID versionBusinessKey,
            CreateDirectoryItemRequest request
    ) {
        DirectoryVersion directoryVersion =
                getDirectoryVersion(
                        versionBusinessKey
                );

        ensureDraft(directoryVersion);

        String code =
                normalizeCode(request.code());

        if (
                directoryItemRepository
                        .existsByDirectoryVersionAndCodeIgnoreCase(
                                directoryVersion,
                                code
                        )
        ) {
            throw new ConflictException(
                    "Directory item with code '"
                            + code
                            + "' already exists in version "
                            + directoryVersion.getVersionNumber()
            );
        }

        DirectoryItem item =
                new DirectoryItem();

        item.setDirectoryVersion(
                directoryVersion
        );
        item.setCode(code);
        item.setName(request.name().trim());
        item.setDescription(
                normalizeNullable(
                        request.description()
                )
        );
        item.setEnabled(
                request.enabled() == null
                        || request.enabled()
        );
        item.setSortOrder(
                request.sortOrder() == null
                        ? 0
                        : request.sortOrder()
        );
        item.setAttributes(
                request.attributes()
        );

        String actor =
                currentActorService.getCurrentActor();

        item.setCreatedBy(actor);
        item.setUpdatedBy(actor);

        DirectoryItem saved =
                directoryItemRepository
                        .saveAndFlush(item);

        return DirectoryMapper.toItem(saved);
    }

    @Transactional
    public DirectoryItemResponse updateItem(
            UUID versionBusinessKey,
            UUID itemBusinessKey,
            UpdateDirectoryItemRequest request
    ) {
        DirectoryVersion directoryVersion =
                getDirectoryVersion(
                        versionBusinessKey
                );

        ensureDraft(directoryVersion);

        DirectoryItem item =
                getItem(itemBusinessKey);

        ensureItemBelongsToVersion(
                item,
                directoryVersion
        );

        String code =
                normalizeCode(request.code());

        if (
                directoryItemRepository
                        .existsByDirectoryVersionAndCodeIgnoreCaseAndBusinessKeyNot(
                                directoryVersion,
                                code,
                                itemBusinessKey
                        )
        ) {
            throw new ConflictException(
                    "Directory item with code '"
                            + code
                            + "' already exists in this version"
            );
        }

        item.setCode(code);
        item.setName(
                request.name().trim()
        );
        item.setDescription(
                normalizeNullable(
                        request.description()
                )
        );

        if (request.enabled() != null) {
            item.setEnabled(
                    request.enabled()
            );
        }

        if (request.sortOrder() != null) {
            item.setSortOrder(
                    request.sortOrder()
            );
        }

        if (request.attributes() != null) {
            item.setAttributes(
                    request.attributes()
            );
        }

        item.setUpdatedBy(
                currentActorService.getCurrentActor()
        );

        directoryItemRepository.flush();

        return DirectoryMapper.toItem(item);
    }

    @Transactional
    public void deleteItem(
            UUID versionBusinessKey,
            UUID itemBusinessKey
    ) {
        DirectoryVersion directoryVersion =
                getDirectoryVersion(
                        versionBusinessKey
                );

        ensureDraft(directoryVersion);

        DirectoryItem item =
                getItem(itemBusinessKey);

        ensureItemBelongsToVersion(
                item,
                directoryVersion
        );

        directoryItemRepository.delete(item);
        directoryItemRepository.flush();
    }

    private DirectoryDetailResponse buildDetailResponse(
            Directory directory
    ) {
        List<DirectoryVersionResponse> versions =
                directoryVersionRepository
                        .findAllByDirectoryOrderByVersionNumberDesc(
                                directory
                        )
                        .stream()
                        .map(this::buildVersionResponse)
                        .toList();

        return DirectoryMapper.toDetail(
                directory,
                versions
        );
    }

    private DirectoryVersionResponse buildVersionResponse(
            DirectoryVersion directoryVersion
    ) {
        long itemCount =
                directoryItemRepository
                        .countByDirectoryVersion(
                                directoryVersion
                        );

        return DirectoryMapper.toVersion(
                directoryVersion,
                itemCount
        );
    }

    private Directory getDirectory(
            UUID businessKey
    ) {
        return directoryRepository
                .findByBusinessKey(
                        businessKey
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Directory not found: "
                                                + businessKey
                                )
                );
    }

    private DirectoryVersion getDirectoryVersion(
            UUID businessKey
    ) {
        return directoryVersionRepository
                .findByBusinessKey(
                        businessKey
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Directory version not found: "
                                                + businessKey
                                )
                );
    }

    private DirectoryItem getItem(
            UUID businessKey
    ) {
        return directoryItemRepository
                .findByBusinessKey(
                        businessKey
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Directory item not found: "
                                                + businessKey
                                )
                );
    }

    private void ensureDraft(
            DirectoryVersion directoryVersion
    ) {
        if (
                directoryVersion.getStatus()
                        != DirectoryVersionStatus.DRAFT
        ) {
            throw new ConflictException(
                    "Only DRAFT directory version can be modified"
            );
        }
    }

    private void ensureItemBelongsToVersion(
            DirectoryItem item,
            DirectoryVersion directoryVersion
    ) {
        if (
                !item.getDirectoryVersion()
                        .getId()
                        .equals(
                                directoryVersion.getId()
                        )
        ) {
            throw new ResourceNotFoundException(
                    "Directory item does not belong to specified version"
            );
        }
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

    private String normalizeCode(
            String value
    ) {
        return value
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeNullable(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }

    private String normalizeSearch(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        return "%"
                + normalized.toLowerCase(Locale.ROOT)
                + "%";
    }
}
