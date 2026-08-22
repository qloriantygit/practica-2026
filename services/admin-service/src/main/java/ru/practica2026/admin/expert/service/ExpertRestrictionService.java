package ru.practica2026.admin.expert.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;

import ru.practica2026.admin.expert.dto.request.ChangeExpertRestrictionStatusRequest;
import ru.practica2026.admin.expert.dto.request.CreateExpertRestrictionRequest;
import ru.practica2026.admin.expert.dto.request.UpdateExpertRestrictionRequest;
import ru.practica2026.admin.expert.dto.response.ExpertRestrictionResponse;

import ru.practica2026.admin.expert.entity.ExpertProfile;
import ru.practica2026.admin.expert.entity.ExpertRestriction;

import ru.practica2026.admin.expert.repository.ExpertProfileRepository;
import ru.practica2026.admin.expert.repository.ExpertRestrictionRepository;

import ru.practica2026.admin.security.service.CurrentActorService;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ExpertRestrictionService {

    private final ExpertProfileRepository expertProfileRepository;
    private final ExpertRestrictionRepository restrictionRepository;
    private final CurrentActorService currentActorService;

    public ExpertRestrictionService(
            ExpertProfileRepository expertProfileRepository,
            ExpertRestrictionRepository restrictionRepository,
            CurrentActorService currentActorService
    ) {
        this.expertProfileRepository = expertProfileRepository;
        this.restrictionRepository = restrictionRepository;
        this.currentActorService = currentActorService;
    }

    @Transactional
    public ExpertRestrictionResponse create(
            UUID expertBusinessKey,
            CreateExpertRestrictionRequest request
    ) {
        ExpertProfile expert =
                getExpert(expertBusinessKey);

        String code =
                normalizeCode(request.code());

        validateValidity(
                request.validFrom(),
                request.validTo()
        );

        if (
                restrictionRepository
                        .existsByExpertProfileAndCodeIgnoreCase(
                                expert,
                                code
                        )
        ) {
            throw new ConflictException(
                    "Expert restriction with code '"
                            + code
                            + "' already exists"
            );
        }

        String actor =
                currentActorService.getCurrentActor();

        ExpertRestriction restriction =
                new ExpertRestriction();

        restriction.setExpertProfile(expert);
        restriction.setCode(code);
        restriction.setDescription(
                request.description().trim()
        );
        restriction.setValidFrom(
                request.validFrom()
        );
        restriction.setValidTo(
                request.validTo()
        );
        restriction.setActive(true);
        restriction.setCreatedBy(actor);
        restriction.setUpdatedBy(actor);

        restrictionRepository
                .saveAndFlush(restriction);

        return toResponse(restriction);
    }

    @Transactional(readOnly = true)
    public List<ExpertRestrictionResponse> findAll(
            UUID expertBusinessKey
    ) {
        ExpertProfile expert =
                getExpert(expertBusinessKey);

        return restrictionRepository
                .findAllByExpertProfileOrderByCodeAsc(
                        expert
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExpertRestrictionResponse get(
            UUID expertBusinessKey,
            UUID restrictionBusinessKey
    ) {
        return toResponse(
                getRestriction(
                        expertBusinessKey,
                        restrictionBusinessKey
                )
        );
    }

    @Transactional
    public ExpertRestrictionResponse update(
            UUID expertBusinessKey,
            UUID restrictionBusinessKey,
            UpdateExpertRestrictionRequest request
    ) {
        ExpertRestriction restriction =
                getRestriction(
                        expertBusinessKey,
                        restrictionBusinessKey
                );

        String code =
                normalizeCode(request.code());

        validateValidity(
                request.validFrom(),
                request.validTo()
        );

        if (
                restrictionRepository
                        .existsByExpertProfileAndCodeIgnoreCaseAndBusinessKeyNot(
                                restriction.getExpertProfile(),
                                code,
                                restrictionBusinessKey
                        )
        ) {
            throw new ConflictException(
                    "Expert restriction with code '"
                            + code
                            + "' already exists"
            );
        }

        restriction.setCode(code);
        restriction.setDescription(
                request.description().trim()
        );
        restriction.setValidFrom(
                request.validFrom()
        );
        restriction.setValidTo(
                request.validTo()
        );
        restriction.setUpdatedBy(
                currentActorService.getCurrentActor()
        );

        restrictionRepository.flush();

        return toResponse(restriction);
    }

    @Transactional
    public ExpertRestrictionResponse changeStatus(
            UUID expertBusinessKey,
            UUID restrictionBusinessKey,
            ChangeExpertRestrictionStatusRequest request
    ) {
        ExpertRestriction restriction =
                getRestriction(
                        expertBusinessKey,
                        restrictionBusinessKey
                );

        restriction.setActive(
                request.active()
        );

        restriction.setUpdatedBy(
                currentActorService.getCurrentActor()
        );

        restrictionRepository.flush();

        return toResponse(restriction);
    }

    private ExpertProfile getExpert(
            UUID businessKey
    ) {
        return expertProfileRepository
                .findByBusinessKey(businessKey)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Expert profile not found: "
                                        + businessKey
                        )
                );
    }

    private ExpertRestriction getRestriction(
            UUID expertBusinessKey,
            UUID restrictionBusinessKey
    ) {
        ExpertRestriction restriction =
                restrictionRepository
                        .findByBusinessKey(
                                restrictionBusinessKey
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Expert restriction not found: "
                                                + restrictionBusinessKey
                                )
                        );

        if (
                !restriction
                        .getExpertProfile()
                        .getBusinessKey()
                        .equals(expertBusinessKey)
        ) {
            throw new ResourceNotFoundException(
                    "Expert restriction not found: "
                            + restrictionBusinessKey
            );
        }

        return restriction;
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
                    "Restriction validTo cannot be before validFrom"
            );
        }
    }

    private String normalizeCode(
            String code
    ) {
        return code
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private ExpertRestrictionResponse toResponse(
            ExpertRestriction restriction
    ) {
        return new ExpertRestrictionResponse(
                restriction.getBusinessKey(),
                restriction
                        .getExpertProfile()
                        .getBusinessKey(),
                restriction.getCode(),
                restriction.getDescription(),
                restriction.getValidFrom(),
                restriction.getValidTo(),
                restriction.isActive(),
                restriction.getVersion(),
                restriction.getCreatedAt(),
                restriction.getUpdatedAt(),
                restriction.getCreatedBy(),
                restriction.getUpdatedBy()
        );
    }
}
