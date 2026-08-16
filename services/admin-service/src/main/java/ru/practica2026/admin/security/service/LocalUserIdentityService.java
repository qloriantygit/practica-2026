package ru.practica2026.admin.security.service;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.user.entity.UserAccount;
import ru.practica2026.admin.user.entity.UserStatus;
import ru.practica2026.admin.user.repository.UserAccountRepository;

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
        String externalId = jwt.getSubject();

        if (
                externalId == null
                ||
                externalId.isBlank()
        ) {
            return Optional.empty();
        }

        Optional<UserAccount> byExternalId =
                userRepository.findByExternalId(
                        externalId
                );

        if (byExternalId.isPresent()) {
            return activeUserId(
                    byExternalId.get()
            );
        }

        String username =
                jwt.getClaimAsString(
                        "preferred_username"
                );

        if (
                username == null
                ||
                username.isBlank()
        ) {
            return Optional.empty();
        }

        Optional<UserAccount> byUsername =
                userRepository.findByUsernameIgnoreCase(
                        username
                );

        if (byUsername.isEmpty()) {
            return Optional.empty();
        }

        UserAccount user = byUsername.get();

        if (
                user.getExternalId() != null
                &&
                !user.getExternalId().equals(externalId)
        ) {
            return Optional.empty();
        }

        if (user.getExternalId() == null) {
            user.setExternalId(externalId);
            user.setUpdatedBy(username);
            userRepository.flush();
        }

        return activeUserId(user);
    }

    private Optional<Long> activeUserId(
            UserAccount user
    ) {
        if (user.getStatus() != UserStatus.ACTIVE) {
            return Optional.empty();
        }

        return Optional.of(user.getId());
    }
}
