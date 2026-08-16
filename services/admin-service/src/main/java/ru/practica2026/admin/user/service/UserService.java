package ru.practica2026.admin.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;
import ru.practica2026.admin.common.response.PageResponse;
import ru.practica2026.admin.organization.entity.Organization;
import ru.practica2026.admin.organization.entity.OrganizationStatus;
import ru.practica2026.admin.organization.repository.OrganizationRepository;
import ru.practica2026.admin.security.service.CurrentActorService;
import ru.practica2026.admin.role.entity.Role;
import ru.practica2026.admin.role.entity.RoleStatus;
import ru.practica2026.admin.role.repository.RoleRepository;
import ru.practica2026.admin.user.dto.request.AssignRoleRequest;
import ru.practica2026.admin.user.dto.request.ChangeUserStatusRequest;
import ru.practica2026.admin.user.dto.request.CreateUserRequest;
import ru.practica2026.admin.user.dto.request.UpdateUserRequest;
import ru.practica2026.admin.user.dto.response.UserDetailResponse;
import ru.practica2026.admin.user.dto.response.UserResponse;
import ru.practica2026.admin.user.entity.UserAccount;
import ru.practica2026.admin.user.entity.UserRole;
import ru.practica2026.admin.user.entity.UserRoleId;
import ru.practica2026.admin.user.entity.UserStatus;
import ru.practica2026.admin.user.mapper.UserMapper;
import ru.practica2026.admin.user.repository.UserAccountRepository;
import ru.practica2026.admin.user.repository.UserRoleRepository;
import ru.practica2026.admin.user.repository.UserSpecification;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "username",
                    "email",
                    "firstName",
                    "lastName",
                    "status",
                    "createdAt",
                    "updatedAt"
            );

    private final UserAccountRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final CurrentActorService currentActorService;

    public UserService(
            UserAccountRepository userRepository,
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository,
            OrganizationRepository organizationRepository,
            CurrentActorService currentActorService
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
        this.currentActorService = currentActorService;
    }

    @Transactional
    public UserResponse create(
            CreateUserRequest request
    ) {
        String username =
                normalizeUsername(request.username());

        String email =
                normalizeEmail(request.email());

        validateUniqueUsername(
                username,
                null
        );

        validateUniqueEmail(
                email,
                null
        );

        Organization organization =
                resolveActiveOrganization(
                        request.organizationBusinessKey()
                );

        UserAccount user =
                new UserAccount();

        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(
                normalizeNullable(request.firstName())
        );
        user.setLastName(
                normalizeNullable(request.lastName())
        );
        user.setOrganization(organization);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedBy(currentActorService.getCurrentActor());
        user.setUpdatedBy(currentActorService.getCurrentActor());

        UserAccount saved =
                userRepository.saveAndFlush(user);

        return UserMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserDetailResponse get(
            UUID businessKey
    ) {
        UserAccount user =
                findUser(businessKey);

        return buildDetailResponse(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> findAll(
            String search,
            UserStatus status,
            UUID organizationBusinessKey,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        validateSortField(sortBy);

        PageRequest pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                direction,
                                sortBy
                        )
                );

        Page<UserResponse> result =
                userRepository
                        .findAll(
                                UserSpecification.withFilters(
                                        search,
                                        status,
                                        organizationBusinessKey
                                ),
                                pageable
                        )
                        .map(UserMapper::toResponse);

        return PageResponse.from(result);
    }

    @Transactional
    public UserResponse update(
            UUID businessKey,
            UpdateUserRequest request
    ) {
        UserAccount user =
                findUser(businessKey);

        String username =
                normalizeUsername(request.username());

        String email =
                normalizeEmail(request.email());

        validateUniqueUsername(
                username,
                businessKey
        );

        validateUniqueEmail(
                email,
                businessKey
        );

        Organization organization =
                resolveActiveOrganization(
                        request.organizationBusinessKey()
                );

        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(
                normalizeNullable(request.firstName())
        );
        user.setLastName(
                normalizeNullable(request.lastName())
        );
        user.setOrganization(organization);
        user.setUpdatedBy(currentActorService.getCurrentActor());

        userRepository.flush();

        return UserMapper.toResponse(user);
    }

    @Transactional
    public UserResponse changeStatus(
            UUID businessKey,
            ChangeUserStatusRequest request
    ) {
        UserAccount user =
                findUser(businessKey);

        if (
                request.status() == UserStatus.ACTIVE
                &&
                user.getOrganization() != null
                &&
                user.getOrganization().getStatus()
                        == OrganizationStatus.ARCHIVED
        ) {
            throw new ConflictException(
                    "User cannot be activated because organization is archived"
            );
        }

        user.setStatus(request.status());
        user.setUpdatedBy(currentActorService.getCurrentActor());

        userRepository.flush();

        return UserMapper.toResponse(user);
    }

    @Transactional
    public UserDetailResponse assignRole(
            UUID userBusinessKey,
            UUID roleBusinessKey,
            AssignRoleRequest request
    ) {
        UserAccount user =
                findUser(userBusinessKey);

        if (user.getStatus() == UserStatus.ARCHIVED) {
            throw new ConflictException(
                    "Role cannot be assigned to archived user"
            );
        }

        Role role =
                findRole(roleBusinessKey);

        if (role.getStatus() != RoleStatus.ACTIVE) {
            throw new ConflictException(
                    "Archived role cannot be assigned"
            );
        }

        Instant now = Instant.now();

        Instant validFrom =
                request != null
                &&
                request.validFrom() != null
                        ? request.validFrom()
                        : now;

        Instant validTo =
                request == null
                        ? null
                        : request.validTo();

        validatePeriod(
                validFrom,
                validTo
        );

        UserRole assignment =
                userRoleRepository
                        .findByUser_IdAndRole_Id(
                                user.getId(),
                                role.getId()
                        )
                        .orElse(null);

        if (assignment != null) {

            boolean stillValid =
                    assignment.getValidTo() == null
                    ||
                    assignment.getValidTo()
                            .isAfter(now);

            if (stillValid) {
                throw new ConflictException(
                        "Role is already assigned to user"
                );
            }

            assignment.setValidFrom(validFrom);
            assignment.setValidTo(validTo);
            assignment.setAssignedAt(now);
            assignment.setAssignedBy(
                    currentActorService.getCurrentActor()
            );
        }
        else {

            assignment =
                    new UserRole();

            assignment.setId(
                    new UserRoleId(
                            user.getId(),
                            role.getId()
                    )
            );

            assignment.setUser(user);
            assignment.setRole(role);
            assignment.setValidFrom(validFrom);
            assignment.setValidTo(validTo);
            assignment.setAssignedAt(now);
            assignment.setAssignedBy(
                    currentActorService.getCurrentActor()
            );
        }

        userRoleRepository
                .saveAndFlush(assignment);

        return buildDetailResponse(user);
    }

    @Transactional
    public void removeRole(
            UUID userBusinessKey,
            UUID roleBusinessKey
    ) {
        UserAccount user =
                findUser(userBusinessKey);

        Role role =
                findRole(roleBusinessKey);

        UserRole assignment =
                userRoleRepository
                        .findByUser_IdAndRole_Id(
                                user.getId(),
                                role.getId()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Role assignment not found"
                                        )
                        );

        userRoleRepository.delete(assignment);
        userRoleRepository.flush();
    }

    private UserDetailResponse buildDetailResponse(
            UserAccount user
    ) {
        List<UserRole> assignments =
                userRoleRepository
                        .findAllByUserIdWithRole(
                                user.getId()
                        );

        return UserMapper.toDetailResponse(
                user,
                assignments
        );
    }

    private UserAccount findUser(
            UUID businessKey
    ) {
        return userRepository
                .findByBusinessKey(businessKey)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "User not found: " +
                                        businessKey
                                )
                );
    }

    private Role findRole(
            UUID businessKey
    ) {
        return roleRepository
                .findByBusinessKey(businessKey)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Role not found: " +
                                        businessKey
                                )
                );
    }

    private Organization resolveActiveOrganization(
            UUID businessKey
    ) {
        Organization organization =
                organizationRepository
                        .findByBusinessKey(businessKey)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Organization not found: " +
                                                businessKey
                                        )
                        );

        if (
                organization.getStatus()
                        != OrganizationStatus.ACTIVE
        ) {
            throw new ConflictException(
                    "Archived organization cannot be assigned to user"
            );
        }

        return organization;
    }

    private void validateUniqueUsername(
            String username,
            UUID currentBusinessKey
    ) {
        boolean exists =
                currentBusinessKey == null
                        ? userRepository
                                .existsByUsernameIgnoreCase(
                                        username
                                )
                        : userRepository
                                .existsByUsernameIgnoreCaseAndBusinessKeyNot(
                                        username,
                                        currentBusinessKey
                                );

        if (exists) {
            throw new ConflictException(
                    "User with username '" +
                    username +
                    "' already exists"
            );
        }
    }

    private void validateUniqueEmail(
            String email,
            UUID currentBusinessKey
    ) {
        boolean exists =
                currentBusinessKey == null
                        ? userRepository
                                .existsByEmailIgnoreCase(
                                        email
                                )
                        : userRepository
                                .existsByEmailIgnoreCaseAndBusinessKeyNot(
                                        email,
                                        currentBusinessKey
                                );

        if (exists) {
            throw new ConflictException(
                    "User with email '" +
                    email +
                    "' already exists"
            );
        }
    }

    private void validatePeriod(
            Instant validFrom,
            Instant validTo
    ) {
        if (
                validTo != null
                &&
                !validTo.isAfter(validFrom)
        ) {
            throw new IllegalArgumentException(
                    "validTo must be later than validFrom"
            );
        }
    }

    private void validateSortField(
            String sortBy
    ) {
        if (
                !ALLOWED_SORT_FIELDS
                        .contains(sortBy)
        ) {
            throw new IllegalArgumentException(
                    "Unsupported user sort field: " +
                    sortBy
            );
        }
    }

    private String normalizeUsername(
            String value
    ) {
        return value
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String normalizeEmail(
            String value
    ) {
        return value
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String normalizeNullable(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String result = value.trim();

        return result.isBlank()
                ? null
                : result;
    }
}
