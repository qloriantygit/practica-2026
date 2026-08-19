package ru.practica2026.admin.security.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;

import ru.practica2026.admin.user.entity.UserAccount;
import ru.practica2026.admin.user.entity.UserStatus;
import ru.practica2026.admin.user.repository.UserAccountRepository;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalUserIdentityServiceTest {

    @Mock
    private UserAccountRepository userRepository;

    @Test
    void linksExistingLocalUserToKeycloakIdentity() {
        LocalUserIdentityService service =
                new LocalUserIdentityService(
                        userRepository
                );

        UserAccount user =
                activeUser();

        when(
                userRepository.findByExternalId(
                        "keycloak-user-id"
                )
        ).thenReturn(
                Optional.empty()
        );

        when(
                userRepository.findByUsernameIgnoreCase(
                        "test.admin"
                )
        ).thenReturn(
                Optional.of(user)
        );

        Optional<Long> result =
                service.resolveAndLinkActiveUserId(
                        jwt(
                                "keycloak-user-id",
                                "test.admin",
                                "admin@example.local",
                                "Test",
                                "Administrator"
                        )
                );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                10L,
                result.get()
        );

        assertEquals(
                "keycloak-user-id",
                user.getExternalId()
        );

        assertEquals(
                "admin@example.local",
                user.getEmail()
        );

        assertEquals(
                "Test",
                user.getFirstName()
        );

        assertEquals(
                "Administrator",
                user.getLastName()
        );

        verify(userRepository)
                .flush();
    }

    @Test
    void synchronizesChangedProfileForLinkedUser() {
        LocalUserIdentityService service =
                new LocalUserIdentityService(
                        userRepository
                );

        UserAccount user =
                activeUser();

        user.setExternalId(
                "keycloak-user-id"
        );

        user.setEmail(
                "old@example.local"
        );

        when(
                userRepository.findByExternalId(
                        "keycloak-user-id"
                )
        ).thenReturn(
                Optional.of(user)
        );

        Optional<Long> result =
                service.resolveAndLinkActiveUserId(
                        jwt(
                                "keycloak-user-id",
                                "test.admin",
                                "new@example.local",
                                "Updated",
                                "Administrator"
                        )
                );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                "new@example.local",
                user.getEmail()
        );

        assertEquals(
                "Updated",
                user.getFirstName()
        );

        verify(userRepository)
                .flush();
    }

    @Test
    void doesNotWriteWhenIdentityDataHasNotChanged() {
        LocalUserIdentityService service =
                new LocalUserIdentityService(
                        userRepository
                );

        UserAccount user =
                activeUser();

        user.setExternalId(
                "keycloak-user-id"
        );

        user.setEmail(
                "admin@example.local"
        );

        user.setFirstName(
                "Test"
        );

        user.setLastName(
                "Administrator"
        );

        when(
                userRepository.findByExternalId(
                        "keycloak-user-id"
                )
        ).thenReturn(
                Optional.of(user)
        );

        Optional<Long> result =
                service.resolveAndLinkActiveUserId(
                        jwt(
                                "keycloak-user-id",
                                "test.admin",
                                "admin@example.local",
                                "Test",
                                "Administrator"
                        )
                );

        assertTrue(
                result.isPresent()
        );

        verify(
                userRepository,
                never()
        ).flush();
    }

    private UserAccount activeUser() {
        UserAccount user =
                new UserAccount();

        ReflectionTestUtils.setField(
                user,
                "id",
                10L
        );

        user.setUsername(
                "test.admin"
        );

        user.setStatus(
                UserStatus.ACTIVE
        );

        return user;
    }

    private Jwt jwt(
            String subject,
            String username,
            String email,
            String firstName,
            String lastName
    ) {
        Instant now =
                Instant.now();

        return Jwt
                .withTokenValue("test-token")
                .header(
                        "alg",
                        "none"
                )
                .subject(subject)
                .issuedAt(now)
                .expiresAt(
                        now.plusSeconds(300)
                )
                .claim(
                        "preferred_username",
                        username
                )
                .claim(
                        "email",
                        email
                )
                .claim(
                        "given_name",
                        firstName
                )
                .claim(
                        "family_name",
                        lastName
                )
                .build();
    }
}
