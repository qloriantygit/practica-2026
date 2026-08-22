package ru.practica2026.admin.savedview.service;


import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;

import ru.practica2026.admin.savedview.dto.request.SaveViewRequest;
import ru.practica2026.admin.savedview.dto.response.SavedViewResponse;
import ru.practica2026.admin.savedview.entity.SavedView;
import ru.practica2026.admin.savedview.repository.SavedViewRepository;

import ru.practica2026.admin.security.service.LocalUserIdentityService;
import ru.practica2026.admin.user.entity.UserAccount;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class SavedViewService {

    private final SavedViewRepository repository;
    private final LocalUserIdentityService identityService;

    public SavedViewService(
            SavedViewRepository repository,
            LocalUserIdentityService identityService
    ) {
        this.repository = repository;
        this.identityService = identityService;
    }

    @Transactional
    public SavedViewResponse create(
            Jwt jwt,
            SaveViewRequest request
    ) {
        UserAccount owner =
                currentUser(jwt);

        String resourceType =
                normalizeResourceType(
                        request.resourceType()
                );

        String name =
                request.name().trim();

        if (
                repository
                        .existsByOwnerAndResourceTypeIgnoreCaseAndNameIgnoreCase(
                                owner,
                                resourceType,
                                name
                        )
        ) {
            throw new ConflictException(
                    "Saved view with name '"
                            + name
                            + "' already exists for resource "
                            + resourceType
            );
        }

        SavedView view =
                new SavedView();

        view.setOwner(owner);

        apply(
                view,
                request
        );

        view.setCreatedBy(
                owner.getUsername()
        );

        view.setUpdatedBy(
                owner.getUsername()
        );

        view = repository.saveAndFlush(view);

        return toResponse(view);
    }

    @Transactional(readOnly = true)
    public List<SavedViewResponse> findAll(
            Jwt jwt,
            String resourceType
    ) {
        UserAccount owner =
                currentUser(jwt);

        List<SavedView> views;

        if (
                resourceType == null
                ||
                resourceType.isBlank()
        ) {
            views =
                    repository
                            .findAllByOwnerOrderByResourceTypeAscNameAsc(
                                    owner
                            );
        }
        else {
            views =
                    repository
                            .findAllByOwnerAndResourceTypeIgnoreCaseOrderByNameAsc(
                                    owner,
                                    normalizeResourceType(
                                            resourceType
                                    )
                            );
        }

        return views
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SavedViewResponse get(
            Jwt jwt,
            UUID businessKey
    ) {
        UserAccount owner =
                currentUser(jwt);

        return toResponse(
                getOwnedView(
                        owner,
                        businessKey
                )
        );
    }

    @Transactional
    public SavedViewResponse update(
            Jwt jwt,
            UUID businessKey,
            SaveViewRequest request
    ) {
        UserAccount owner =
                currentUser(jwt);

        SavedView view =
                getOwnedView(
                        owner,
                        businessKey
                );

        String resourceType =
                normalizeResourceType(
                        request.resourceType()
                );

        String name =
                request.name().trim();

        if (
                repository
                        .existsByOwnerAndResourceTypeIgnoreCaseAndNameIgnoreCaseAndBusinessKeyNot(
                                owner,
                                resourceType,
                                name,
                                businessKey
                        )
        ) {
            throw new ConflictException(
                    "Saved view with name '"
                            + name
                            + "' already exists for resource "
                            + resourceType
            );
        }

        apply(
                view,
                request
        );

        view.setUpdatedBy(
                owner.getUsername()
        );

        repository.flush();

        return toResponse(view);
    }

    @Transactional
    public void delete(
            Jwt jwt,
            UUID businessKey
    ) {
        UserAccount owner =
                currentUser(jwt);

        SavedView view =
                getOwnedView(
                        owner,
                        businessKey
                );

        repository.delete(view);
        repository.flush();
    }

    private UserAccount currentUser(
            Jwt jwt
    ) {
        return identityService
                .synchronizeCurrentIdentity(jwt);
    }

    private SavedView getOwnedView(
            UserAccount owner,
            UUID businessKey
    ) {
        return repository
                .findByBusinessKeyAndOwner(
                        businessKey,
                        owner
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Saved view not found: "
                                        + businessKey
                        )
                );
    }

    private void apply(
            SavedView view,
            SaveViewRequest request
    ) {
        view.setName(
                request.name().trim()
        );

        view.setResourceType(
                normalizeResourceType(
                        request.resourceType()
                )
        );

        view.setFilters(
                new LinkedHashMap<>(
                        request.filters()
                )
        );

        String sortBy =
                normalizeNullable(
                        request.sortBy()
                );

        view.setSortBy(sortBy);

        if (sortBy == null) {
            view.setSortDirection(null);
        }
        else {
            String direction =
                    normalizeNullable(
                            request.sortDirection()
                    );

            view.setSortDirection(
                    direction == null
                            ? "ASC"
                            : direction.toUpperCase(
                                    Locale.ROOT
                            )
            );
        }
    }

    private String normalizeResourceType(
            String value
    ) {
        return value
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }

    private String normalizeNullable(
            String value
    ) {
        if (
                value == null
                ||
                value.isBlank()
        ) {
            return null;
        }

        return value.trim();
    }

    private SavedViewResponse toResponse(
            SavedView view
    ) {
        return new SavedViewResponse(
                view.getBusinessKey(),
                view.getName(),
                view.getResourceType(),
                new LinkedHashMap<>(
                        view.getFilters()
                ),
                view.getSortBy(),
                view.getSortDirection(),
                view.getVersion(),
                view.getCreatedAt(),
                view.getUpdatedAt(),
                view.getCreatedBy(),
                view.getUpdatedBy()
        );
    }
}
