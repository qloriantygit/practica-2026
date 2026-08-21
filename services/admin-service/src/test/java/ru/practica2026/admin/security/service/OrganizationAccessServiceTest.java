package ru.practica2026.admin.security.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import ru.practica2026.admin.organization.entity.Organization;
import ru.practica2026.admin.user.entity.UserAccount;
import ru.practica2026.admin.user.entity.UserStatus;
import ru.practica2026.admin.user.repository.UserAccountRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationAccessServiceTest {

    @Mock
    private UserAccountRepository userRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void allowsUserToAccessOwnOrganization() {
        OrganizationAccessService service =
                new OrganizationAccessService(userRepository);

        UUID organizationKey =
                UUID.randomUUID();

        UserAccount user =
                activeUser(
                        organizationKey
                );

        authenticate();

        when(
                userRepository.findByExternalId(
                        "keycloak-user-id"
                )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                userRepository.hasActiveAdminRole(
                        10L
                )
        ).thenReturn(false);

        assertDoesNotThrow(
                () ->
                        service.requireAccess(
                                organizationKey
                        )
        );
    }

    @Test
    void deniesUserAccessToAnotherOrganization() {
        OrganizationAccessService service =
                new OrganizationAccessService(userRepository);

        UserAccount user =
                activeUser(
                        UUID.randomUUID()
                );

        authenticate();

        when(
                userRepository.findByExternalId(
                        "keycloak-user-id"
                )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                userRepository.hasActiveAdminRole(
                        10L
                )
        ).thenReturn(false);

        assertThrows(
                AccessDeniedException.class,
                () ->
                        service.requireAccess(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void allowsAdminToAccessAnotherOrganization() {
        OrganizationAccessService service =
                new OrganizationAccessService(userRepository);

        UserAccount user =
                activeUser(
                        UUID.randomUUID()
                );

        authenticate();

        when(
                userRepository.findByExternalId(
                        "keycloak-user-id"
                )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                userRepository.hasActiveAdminRole(
                        10L
                )
        ).thenReturn(true);

        assertDoesNotThrow(
                () ->
                        service.requireAccess(
                                UUID.randomUUID()
                        )
        );
    }

    private UserAccount activeUser(
            UUID organizationBusinessKey
    ) {
        Organization organization =
                new Organization();

        ReflectionTestUtils.setField(
                organization,
                "businessKey",
                organizationBusinessKey
        );

        UserAccount user =
                new UserAccount();

        ReflectionTestUtils.setField(
                user,
                "id",
                10L
        );

        user.setUsername(
                "analyst.user"
        );

        user.setExternalId(
                "keycloak-user-id"
        );

        user.setStatus(
                UserStatus.ACTIVE
        );

        user.setOrganization(
                organization
        );

        return user;
    }

    private void authenticate() {
        Instant now =
                Instant.now();

        Jwt jwt =
                Jwt.withTokenValue("test-token")
                        .header(
                                "alg",
                                "none"
                        )
                        .subject(
                                "keycloak-user-id"
                        )
                        .issuedAt(now)
                        .expiresAt(
                                now.plusSeconds(300)
                        )
                        .claim(
                                "preferred_username",
                                "analyst.user"
                        )
                        .build();

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        new JwtAuthenticationToken(jwt, List.of())
                );
    }
}
