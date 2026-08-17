package ru.practica2026.admin.expert.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;
import ru.practica2026.admin.directory.entity.DirectoryItem;
import ru.practica2026.admin.directory.entity.DirectoryVersionStatus;
import ru.practica2026.admin.directory.repository.DirectoryItemRepository;
import ru.practica2026.admin.expert.dto.request.AddExpertCompetencyRequest;
import ru.practica2026.admin.expert.dto.request.CreateExpertProfileRequest;
import ru.practica2026.admin.expert.dto.request.UpdateExpertProfileRequest;
import ru.practica2026.admin.expert.dto.response.ExpertCompetencyResponse;
import ru.practica2026.admin.expert.dto.response.ExpertProfilePageResponse;
import ru.practica2026.admin.expert.dto.response.ExpertProfileResponse;
import ru.practica2026.admin.expert.entity.ExpertCompetency;
import ru.practica2026.admin.expert.entity.ExpertProfile;
import ru.practica2026.admin.expert.entity.ExpertProfileStatus;
import ru.practica2026.admin.expert.repository.ExpertCompetencyRepository;
import ru.practica2026.admin.expert.repository.ExpertProfileRepository;
import ru.practica2026.admin.security.service.CurrentActorService;
import ru.practica2026.admin.user.entity.UserAccount;
import ru.practica2026.admin.user.entity.UserStatus;
import ru.practica2026.admin.user.repository.UserAccountRepository;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ExpertProfileService {

    private static final Set<String> SORT_FIELDS =
            Set.of(
                    "specialization",
                    "status",
                    "available",
                    "createdAt",
                    "updatedAt"
            );

    private final ExpertProfileRepository profileRepository;
    private final ExpertCompetencyRepository competencyRepository;
    private final UserAccountRepository userRepository;
    private final DirectoryItemRepository directoryItemRepository;
    private final CurrentActorService currentActorService;

    public ExpertProfileService(
            ExpertProfileRepository profileRepository,
            ExpertCompetencyRepository competencyRepository,
            UserAccountRepository userRepository,
            DirectoryItemRepository directoryItemRepository,
            CurrentActorService currentActorService
    ) {
        this.profileRepository = profileRepository;
        this.competencyRepository = competencyRepository;
        this.userRepository = userRepository;
        this.directoryItemRepository = directoryItemRepository;
        this.currentActorService = currentActorService;
    }

    @Transactional
    public ExpertProfileResponse create(
            CreateExpertProfileRequest request
    ) {
        UserAccount user =
                userRepository
                        .findByBusinessKey(
                                request.userBusinessKey()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found: "
                                                        + request.userBusinessKey()
                                        )
                        );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ConflictException(
                    "Expert profile can be created only for ACTIVE user"
            );
        }

        if (profileRepository.existsByUser(user)) {
            throw new ConflictException(
                    "Expert profile for user already exists"
            );
        }

        String actor =
                currentActorService.getCurrentActor();

        ExpertProfile profile =
                new ExpertProfile();

        profile.setUser(user);

        profile.setSpecialization(
                request.specialization().trim()
        );

        profile.setBio(
                normalizeNullable(
                        request.bio()
                )
        );

        profile.setStatus(
                ExpertProfileStatus.ACTIVE
        );

        profile.setAvailable(
                request.available() == null
                        || request.available()
        );

        profile.setCreatedBy(actor);
        profile.setUpdatedBy(actor);

        ExpertProfile saved =
                profileRepository.saveAndFlush(
                        profile
                );

        return buildResponse(saved);
    }

    @Transactional(readOnly = true)
    public ExpertProfilePageResponse findAll(
            String search,
            ExpertProfileStatus status,
            Boolean available,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        int safePage =
                Math.max(page, 0);

        int safeSize =
                Math.min(
                        Math.max(size, 1),
                        100
                );

        String safeSortBy =
                SORT_FIELDS.contains(sortBy)
                        ? sortBy
                        : "specialization";

        Sort.Direction sortDirection =
                "DESC".equalsIgnoreCase(direction)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Page<ExpertProfile> result =
                profileRepository.search(
                        normalizeSearch(search),
                        status,
                        available,
                        PageRequest.of(
                                safePage,
                                safeSize,
                                Sort.by(
                                        sortDirection,
                                        safeSortBy
                                )
                        )
                );

        return new ExpertProfilePageResponse(
                result.getContent()
                        .stream()
                        .map(this::buildResponse)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public ExpertProfileResponse findByBusinessKey(
            UUID businessKey
    ) {
        return buildResponse(
                getProfile(businessKey)
        );
    }

    @Transactional
    public ExpertProfileResponse update(
            UUID businessKey,
            UpdateExpertProfileRequest request
    ) {
        ExpertProfile profile =
                getProfile(businessKey);

        profile.setSpecialization(
                request.specialization().trim()
        );

        profile.setBio(
                normalizeNullable(
                        request.bio()
                )
        );

        profile.setStatus(
                request.status()
        );

        profile.setAvailable(
                request.available()
        );

        profile.setUpdatedBy(
                currentActorService.getCurrentActor()
        );

        profileRepository.flush();

        return buildResponse(profile);
    }

    @Transactional
    public ExpertCompetencyResponse addCompetency(
            UUID profileBusinessKey,
            AddExpertCompetencyRequest request
    ) {
        ExpertProfile profile =
                getProfile(profileBusinessKey);

        DirectoryItem sourceItem =
                directoryItemRepository
                        .findByBusinessKey(
                                request.directoryItemBusinessKey()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Directory item not found: "
                                                        + request.directoryItemBusinessKey()
                                        )
                        );

        if (
                sourceItem
                        .getDirectoryVersion()
                        .getStatus()
                != DirectoryVersionStatus.PUBLISHED
        ) {
            throw new ConflictException(
                    "Expert competency must reference an item from PUBLISHED directory version"
            );
        }

        if (
                competencyRepository
                        .existsByProfileAndSourceItem(
                                profile,
                                sourceItem
                        )
        ) {
            throw new ConflictException(
                    "Expert already has this competency"
            );
        }

        String actor =
                currentActorService.getCurrentActor();

        ExpertCompetency competency =
                new ExpertCompetency();

        competency.setProfile(profile);
        competency.setSourceItem(sourceItem);
        competency.setProficiencyLevel(
                request.proficiencyLevel()
        );
        competency.setNote(
                normalizeNullable(
                        request.note()
                )
        );

        competency.setCreatedBy(actor);
        competency.setUpdatedBy(actor);

        ExpertCompetency saved =
                competencyRepository
                        .saveAndFlush(
                                competency
                        );

        return toCompetencyResponse(saved);
    }

    @Transactional
    public void deleteCompetency(
            UUID profileBusinessKey,
            UUID competencyBusinessKey
    ) {
        ExpertProfile profile =
                getProfile(
                        profileBusinessKey
                );

        ExpertCompetency competency =
                competencyRepository
                        .findByBusinessKey(
                                competencyBusinessKey
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Expert competency not found: "
                                                        + competencyBusinessKey
                                        )
                        );

        if (
                !competency.getProfile()
                        .getId()
                        .equals(profile.getId())
        ) {
            throw new ResourceNotFoundException(
                    "Expert competency does not belong to specified profile"
            );
        }

        competencyRepository.delete(
                competency
        );

        competencyRepository.flush();
    }

    private ExpertProfileResponse buildResponse(
            ExpertProfile profile
    ) {
        List<ExpertCompetencyResponse> competencies =
                competencyRepository
                        .findAllForProfile(profile)
                        .stream()
                        .map(
                                this::toCompetencyResponse
                        )
                        .toList();

        return new ExpertProfileResponse(
                profile.getBusinessKey(),
                profile.getUser()
                        .getBusinessKey(),
                profile.getUser()
                        .getUsername(),
                profile.getUser()
                        .getEmail(),
                profile.getSpecialization(),
                profile.getBio(),
                profile.getStatus(),
                profile.isAvailable(),
                competencies,
                profile.getVersion(),
                profile.getCreatedAt(),
                profile.getUpdatedAt(),
                profile.getCreatedBy(),
                profile.getUpdatedBy()
        );
    }

    private ExpertCompetencyResponse toCompetencyResponse(
            ExpertCompetency competency
    ) {
        DirectoryItem item =
                competency.getSourceItem();

        return new ExpertCompetencyResponse(
                competency.getBusinessKey(),
                item.getBusinessKey(),
                item.getDirectoryVersion()
                        .getBusinessKey(),
                item.getCode(),
                item.getName(),
                competency.getProficiencyLevel(),
                competency.getNote(),
                competency.getCreatedAt(),
                competency.getCreatedBy()
        );
    }

    private ExpertProfile getProfile(
            UUID businessKey
    ) {
        return profileRepository
                .findByBusinessKey(
                        businessKey
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Expert profile not found: "
                                                + businessKey
                                )
                );
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
                + normalized.toLowerCase(
                        Locale.ROOT
                )
                + "%";
    }
}
