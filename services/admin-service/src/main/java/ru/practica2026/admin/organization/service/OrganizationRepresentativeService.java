package ru.practica2026.admin.organization.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;

import ru.practica2026.admin.organization.dto.request.ChangeOrganizationRepresentativeStatusRequest;
import ru.practica2026.admin.organization.dto.request.CreateOrganizationRepresentativeRequest;
import ru.practica2026.admin.organization.dto.request.UpdateOrganizationRepresentativeRequest;
import ru.practica2026.admin.organization.dto.response.OrganizationRepresentativeResponse;

import ru.practica2026.admin.organization.entity.Organization;
import ru.practica2026.admin.organization.entity.OrganizationRepresentative;
import ru.practica2026.admin.organization.entity.OrganizationStatus;

import ru.practica2026.admin.organization.repository.OrganizationRepository;
import ru.practica2026.admin.organization.repository.OrganizationRepresentativeRepository;

import ru.practica2026.admin.security.service.CurrentActorService;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class OrganizationRepresentativeService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationRepresentativeRepository representativeRepository;
    private final CurrentActorService currentActorService;

    public OrganizationRepresentativeService(
            OrganizationRepository organizationRepository,
            OrganizationRepresentativeRepository representativeRepository,
            CurrentActorService currentActorService
    ) {
        this.organizationRepository =
                organizationRepository;

        this.representativeRepository =
                representativeRepository;

        this.currentActorService =
                currentActorService;
    }

    @Transactional
    public OrganizationRepresentativeResponse create(
            UUID organizationBusinessKey,
            CreateOrganizationRepresentativeRequest request
    ) {
        Organization organization =
                getOrganization(
                        organizationBusinessKey
                );

        if (
                organization.getStatus()
                        != OrganizationStatus.ACTIVE
        ) {
            throw new ConflictException(
                    "Representative cannot be added to an archived organization"
            );
        }

        String email =
                normalizeEmail(
                        request.email()
                );

        if (
                representativeRepository
                        .existsByOrganizationAndEmailIgnoreCase(
                                organization,
                                email
                        )
        ) {
            throw new ConflictException(
                    "Representative with email '"
                            + email
                            + "' already exists in organization"
            );
        }

        String actor =
                currentActorService
                        .getCurrentActor();

        OrganizationRepresentative representative =
                new OrganizationRepresentative();

        representative.setOrganization(
                organization
        );

        apply(
                representative,
                request.firstName(),
                request.lastName(),
                request.middleName(),
                request.position(),
                email,
                request.phone()
        );

        representative.setActive(true);
        representative.setCreatedBy(actor);
        representative.setUpdatedBy(actor);

        representativeRepository
                .saveAndFlush(representative);

        return toResponse(
                representative
        );
    }

    @Transactional(readOnly = true)
    public List<OrganizationRepresentativeResponse> findAll(
            UUID organizationBusinessKey,
            String search
    ) {
        Organization organization =
                getOrganization(
                        organizationBusinessKey
                );

        String normalizedSearch =
                normalizeNullable(search);

        return representativeRepository
                .findAllByOrganizationOrderByLastNameAscFirstNameAsc(
                        organization
                )
                .stream()
                .filter(
                        representative ->
                                matches(
                                        representative,
                                        normalizedSearch
                                )
                )
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrganizationRepresentativeResponse get(
            UUID organizationBusinessKey,
            UUID representativeBusinessKey
    ) {
        return toResponse(
                getRepresentative(
                        organizationBusinessKey,
                        representativeBusinessKey
                )
        );
    }

    @Transactional
    public OrganizationRepresentativeResponse update(
            UUID organizationBusinessKey,
            UUID representativeBusinessKey,
            UpdateOrganizationRepresentativeRequest request
    ) {
        OrganizationRepresentative representative =
                getRepresentative(
                        organizationBusinessKey,
                        representativeBusinessKey
                );

        String email =
                normalizeEmail(
                        request.email()
                );

        if (
                representativeRepository
                        .existsByOrganizationAndEmailIgnoreCaseAndBusinessKeyNot(
                                representative.getOrganization(),
                                email,
                                representativeBusinessKey
                        )
        ) {
            throw new ConflictException(
                    "Representative with email '"
                            + email
                            + "' already exists in organization"
            );
        }

        apply(
                representative,
                request.firstName(),
                request.lastName(),
                request.middleName(),
                request.position(),
                email,
                request.phone()
        );

        representative.setUpdatedBy(
                currentActorService
                        .getCurrentActor()
        );

        representativeRepository.flush();

        return toResponse(
                representative
        );
    }

    @Transactional
    public OrganizationRepresentativeResponse changeStatus(
            UUID organizationBusinessKey,
            UUID representativeBusinessKey,
            ChangeOrganizationRepresentativeStatusRequest request
    ) {
        OrganizationRepresentative representative =
                getRepresentative(
                        organizationBusinessKey,
                        representativeBusinessKey
                );

        representative.setActive(
                request.active()
        );

        representative.setUpdatedBy(
                currentActorService
                        .getCurrentActor()
        );

        representativeRepository.flush();

        return toResponse(
                representative
        );
    }

    private Organization getOrganization(
            UUID businessKey
    ) {
        return organizationRepository
                .findByBusinessKey(businessKey)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Organization not found: "
                                                + businessKey
                                )
                );
    }

    private OrganizationRepresentative getRepresentative(
            UUID organizationBusinessKey,
            UUID representativeBusinessKey
    ) {
        OrganizationRepresentative representative =
                representativeRepository
                        .findByBusinessKey(
                                representativeBusinessKey
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Organization representative not found: "
                                                        + representativeBusinessKey
                                        )
                        );

        if (
                !representative
                        .getOrganization()
                        .getBusinessKey()
                        .equals(
                                organizationBusinessKey
                        )
        ) {
            throw new ResourceNotFoundException(
                    "Organization representative not found: "
                            + representativeBusinessKey
            );
        }

        return representative;
    }

    private void apply(
            OrganizationRepresentative representative,
            String firstName,
            String lastName,
            String middleName,
            String position,
            String email,
            String phone
    ) {
        representative.setFirstName(
                firstName.trim()
        );

        representative.setLastName(
                lastName.trim()
        );

        representative.setMiddleName(
                normalizeNullable(
                        middleName
                )
        );

        representative.setPosition(
                position.trim()
        );

        representative.setEmail(email);

        representative.setPhone(
                normalizeNullable(
                        phone
                )
        );
    }

    private boolean matches(
            OrganizationRepresentative representative,
            String search
    ) {
        if (search == null) {
            return true;
        }

        String value =
                search.toLowerCase(
                        Locale.ROOT
                );

        return contains(
                representative.getFirstName(),
                value
        )
                ||
                contains(
                        representative.getLastName(),
                        value
                )
                ||
                contains(
                        representative.getMiddleName(),
                        value
                )
                ||
                contains(
                        representative.getPosition(),
                        value
                )
                ||
                contains(
                        representative.getEmail(),
                        value
                )
                ||
                contains(
                        representative.getPhone(),
                        value
                );
    }

    private boolean contains(
            String source,
            String search
    ) {
        return source != null
                &&
                source.toLowerCase(
                        Locale.ROOT
                ).contains(search);
    }

    private String normalizeEmail(
            String email
    ) {
        return email
                .trim()
                .toLowerCase(
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

    private OrganizationRepresentativeResponse toResponse(
            OrganizationRepresentative representative
    ) {
        return new OrganizationRepresentativeResponse(
                representative.getBusinessKey(),
                representative
                        .getOrganization()
                        .getBusinessKey(),
                representative
                        .getOrganization()
                        .getCode(),
                representative.getFirstName(),
                representative.getLastName(),
                representative.getMiddleName(),
                representative.getPosition(),
                representative.getEmail(),
                representative.getPhone(),
                representative.isActive(),
                representative.getVersion(),
                representative.getCreatedBy(),
                representative.getUpdatedBy()
        );
    }
}
