package ru.practica2026.admin.security.service;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.user.entity.UserAccount;
import ru.practica2026.admin.user.entity.UserStatus;
import ru.practica2026.admin.user.repository.UserAccountRepository;

import java.util.Objects;
import java.util.Optional;

@Service
public class LocalUserIdentityService {

    private final UserAccountRepository userRepository;

    public LocalUserIdentityService(
            UserAccountRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<Long> resolveAndLinkActiveUserId(
            Jwt jwt
    ) {
        String externalId =
                normalize(
                        jwt.getSubject()
                );

        if (externalId == null) {
            return Optional.empty();
        }

        Optional<UserAccount> byExternalId =
                userRepository.findByExternalId(
                        externalId
                );

        if (byExternalId.isPresent()) {
            UserAccount user =
                    byExternalId.get();

            synchronizeProfile(
                    user,
                    jwt
            );

            return activeUserId(user);
        }

        String username =
                normalize(
                        jwt.getClaimAsString(
                                "preferred_username"
                        )
                );

        if (username == null) {
            return Optional.empty();
        }

        Optional<UserAccount> byUsername =
                userRepository
                        .findByUsernameIgnoreCase(
                                username
                        );

        if (byUsername.isEmpty()) {
            return Optional.empty();
        }

        UserAccount user =
                byUsername.get();

        if (
                user.getExternalId() != null
                &&
                !user.getExternalId()
                        .equals(externalId)
        ) {
            return Optional.empty();
        }

        boolean changed = false;

        if (user.getExternalId() == null) {
            user.setExternalId(
                    externalId
            );

            changed = true;
        }

        changed =
                synchronizeProfileFields(
                        user,
                        jwt
                )
                || changed;

        if (changed) {
            user.setUpdatedBy(
                    username
            );

            userRepository.flush();
        }

        return activeUserId(user);
    }

    private void synchronizeProfile(
            UserAccount user,
            Jwt jwt
    ) {
        boolean changed =
                synchronizeProfileFields(
                        user,
                        jwt
                );

        if (!changed) {
            return;
        }

        String actor =
                normalize(
                        jwt.getClaimAsString(
                                "preferred_username"
                        )
                );

        user.setUpdatedBy(
                actor != null
                        ? actor
                        : user.getUsername()
        );

        userRepository.flush();
    }

    private boolean synchronizeProfileFields(
            UserAccount user,
            Jwt jwt
    ) {
        boolean changed = false;

        String email =
                normalize(
                        jwt.getClaimAsString(
                                "email"
                        )
                );

        String firstName =
                normalize(
                        jwt.getClaimAsString(
                                "given_name"
                        )
                );

        String lastName =
                normalize(
                        jwt.getClaimAsString(
                                "family_name"
                        )
                );

        if (
                email != null
                &&
                !Objects.equals(
                        user.getEmail(),
                        email
                )
        ) {
            user.setEmail(email);
            changed = true;
        }

        if (
                firstName != null
                &&
                !Objects.equals(
                        user.getFirstName(),
                        firstName
                )
        ) {
            user.setFirstName(
                    firstName
            );

            changed = true;
        }

        if (
                lastName != null
                &&
                !Objects.equals(
                        user.getLastName(),
                        lastName
                )
        ) {
            user.setLastName(
                    lastName
            );

            changed = true;
        }

        return changed;
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

    private Optional<Long> activeUserId(
            UserAccount user
    ) {
        if (
                user.getStatus()
                        != UserStatus.ACTIVE
        ) {
            return Optional.empty();
        }

        return Optional.of(
                user.getId()
        );
    }

    @Transactional
    public UserAccount synchronizeCurrentIdentity(
            Jwt jwt
    ) {
        Optional<Long> resolvedUserId =
                resolveAndLinkActiveUserId(jwt);

        if (resolvedUserId.isEmpty()) {
            throw new IllegalStateException(
                    "Authenticated IdP user cannot be synchronized with a local active user"
            );
        }

        String externalId =
                normalize(jwt.getSubject());

        return userRepository
                .findByExternalId(externalId)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Synchronized local user was not found"
                        )
                );
    }
}
