package ru.practica2026.admin.security.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.user.entity.UserAccount;
import ru.practica2026.admin.user.entity.UserStatus;
import ru.practica2026.admin.user.repository.UserAccountRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationAccessService {

    private final UserAccountRepository userRepository;

    public OrganizationAccessService(
            UserAccountRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public void requireAccess(
            UUID organizationBusinessKey
    ) {
        UserAccount user =
                currentUser()
                        .orElseThrow(
                                () -> new AccessDeniedException(
                                        "Authenticated user is not linked to a local account"
                                )
                        );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccessDeniedException(
                    "Local user is not active"
            );
        }

        if (
                userRepository.hasActiveAdminRole(
                        user.getId()
                )
        ) {
            return;
        }

        if (
                user.getOrganization() == null
                ||
                !organizationBusinessKey.equals(
                        user.getOrganization()
                                .getBusinessKey()
                )
        ) {
            throw new AccessDeniedException(
                    "Access to another organization is forbidden"
            );
        }
    }

    private Optional<UserAccount> currentUser() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                !(authentication instanceof JwtAuthenticationToken jwtAuthentication)
                ||
                !authentication.isAuthenticated()
        ) {
            return Optional.empty();
        }

        String externalId =
                normalize(
                        jwtAuthentication
                                .getToken()
                                .getSubject()
                );

        if (externalId != null) {
            Optional<UserAccount> byExternalId =
                    userRepository.findByExternalId(
                            externalId
                    );

            if (byExternalId.isPresent()) {
                return byExternalId;
            }
        }

        String username =
                normalize(
                        jwtAuthentication
                                .getToken()
                                .getClaimAsString(
                                        "preferred_username"
                                )
                );

        if (username == null) {
            return Optional.empty();
        }

        return userRepository
                .findByUsernameIgnoreCase(
                        username
                );
    }

    private String normalize(
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
}
