package ru.practica2026.admin.organization.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;
import ru.practica2026.admin.common.response.PageResponse;
import ru.practica2026.admin.organization.dto.request.ChangeOrganizationStatusRequest;
import ru.practica2026.admin.organization.dto.request.CreateOrganizationRequest;
import ru.practica2026.admin.organization.dto.request.UpdateOrganizationRequest;
import ru.practica2026.admin.organization.dto.response.OrganizationResponse;
import ru.practica2026.admin.organization.entity.Organization;
import ru.practica2026.admin.organization.entity.OrganizationStatus;
import ru.practica2026.admin.organization.mapper.OrganizationMapper;
import ru.practica2026.admin.organization.repository.OrganizationRepository;
import ru.practica2026.admin.organization.repository.OrganizationSpecification;
import ru.practica2026.admin.security.service.CurrentActorService;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class OrganizationService {
private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "code",
                    "name",
                    "status",
                    "createdAt",
                    "updatedAt"
            );

    private final OrganizationRepository organizationRepository;
    private final CurrentActorService currentActorService;

    public OrganizationService(
            OrganizationRepository organizationRepository,
            CurrentActorService currentActorService
    ) {
        this.organizationRepository = organizationRepository;
        this.currentActorService = currentActorService;
    }

    @Transactional
    public OrganizationResponse create(
            CreateOrganizationRequest request
    ) {
        String normalizedCode = normalizeCode(request.code());

        if (organizationRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new ConflictException(
                    "Organization with code '" +
                    normalizedCode +
                    "' already exists"
            );
        }

        Organization organization = new Organization();

        organization.setCode(normalizedCode);
        organization.setName(request.name().trim());

        organization.setParent(
                resolveParent(
                        request.parentBusinessKey(),
                        null
                )
        );

        organization.setStatus(OrganizationStatus.ACTIVE);
        organization.setCreatedBy(currentActorService.getCurrentActor());
        organization.setUpdatedBy(currentActorService.getCurrentActor());

        Organization saved =
                organizationRepository.save(organization);

        return OrganizationMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrganizationResponse get(
            UUID businessKey
    ) {
        return OrganizationMapper.toResponse(
                findByBusinessKey(businessKey)
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<OrganizationResponse> findAll(
            String search,
            OrganizationStatus status,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        validateSortField(sortBy);

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortBy)
        );

        Page<OrganizationResponse> result =
                organizationRepository
                        .findAll(
                                OrganizationSpecification.withFilters(
                                        search,
                                        status
                                ),
                                pageable
                        )
                        .map(OrganizationMapper::toResponse);

        return PageResponse.from(result);
    }

    @Transactional
    public OrganizationResponse update(
            UUID businessKey,
            UpdateOrganizationRequest request
    ) {
        Organization organization =
                findByBusinessKey(businessKey);

        String normalizedCode =
                normalizeCode(request.code());

        if (
                organizationRepository
                        .existsByCodeIgnoreCaseAndBusinessKeyNot(
                                normalizedCode,
                                businessKey
                        )
        ) {
            throw new ConflictException(
                    "Organization with code '" +
                    normalizedCode +
                    "' already exists"
            );
        }

        Organization parent =
                resolveParent(
                        request.parentBusinessKey(),
                        organization
                );

        validateNoHierarchyCycle(
                organization,
                parent
        );

        organization.setCode(normalizedCode);
        organization.setName(request.name().trim());
        organization.setParent(parent);
        organization.setUpdatedBy(currentActorService.getCurrentActor());

        organizationRepository.flush();

        return OrganizationMapper.toResponse(organization);
    }

    @Transactional
    public OrganizationResponse changeStatus(
            UUID businessKey,
            ChangeOrganizationStatusRequest request
    ) {
        Organization organization =
                findByBusinessKey(businessKey);

        if (
                request.status() == OrganizationStatus.ARCHIVED &&
                organizationRepository.existsByParent_IdAndStatus(
                        organization.getId(),
                        OrganizationStatus.ACTIVE
                )
        ) {
            throw new ConflictException(
                    "Organization cannot be archived while it has active child organizations"
            );
        }

        if (
                request.status() == OrganizationStatus.ACTIVE &&
                organization.getParent() != null &&
                organization.getParent().getStatus()
                        == OrganizationStatus.ARCHIVED
        ) {
            throw new ConflictException(
                    "Organization cannot be activated because its parent is archived"
            );
        }

        organization.setStatus(request.status());
        organization.setUpdatedBy(currentActorService.getCurrentActor());

        organizationRepository.flush();

        return OrganizationMapper.toResponse(organization);
    }

    private Organization findByBusinessKey(
            UUID businessKey
    ) {
        return organizationRepository
                .findByBusinessKey(businessKey)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Organization not found: " +
                                businessKey
                        )
                );
    }

    private Organization resolveParent(
            UUID parentBusinessKey,
            Organization currentOrganization
    ) {
        if (parentBusinessKey == null) {
            return null;
        }

        if (
                currentOrganization != null &&
                parentBusinessKey.equals(
                        currentOrganization.getBusinessKey()
                )
        ) {
            throw new ConflictException(
                    "Organization cannot be its own parent"
            );
        }

        Organization parent =
                findByBusinessKey(parentBusinessKey);

        if (
                parent.getStatus()
                        == OrganizationStatus.ARCHIVED
        ) {
            throw new ConflictException(
                    "Archived organization cannot be used as parent"
            );
        }

        return parent;
    }

    private void validateNoHierarchyCycle(
            Organization organization,
            Organization parent
    ) {
        Organization current = parent;

        while (current != null) {

            if (
                    current.getId()
                            .equals(organization.getId())
            ) {
                throw new ConflictException(
                        "Organization hierarchy contains a cycle"
                );
            }

            current = current.getParent();
        }
    }

    private String normalizeCode(
            String code
    ) {
        return code
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private void validateSortField(
            String sortBy
    ) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Unsupported organization sort field: " +
                    sortBy
            );
        }
    }
}
