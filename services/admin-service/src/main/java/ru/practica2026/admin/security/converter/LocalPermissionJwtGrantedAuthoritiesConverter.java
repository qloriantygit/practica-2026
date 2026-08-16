package ru.practica2026.admin.security.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import ru.practica2026.admin.role.repository.PermissionRepository;
import ru.practica2026.admin.security.service.LocalUserIdentityService;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class LocalPermissionJwtGrantedAuthoritiesConverter
        implements Converter<
                Jwt,
                Collection<GrantedAuthority>
        > {

    private final LocalUserIdentityService identityService;
    private final PermissionRepository permissionRepository;

    public LocalPermissionJwtGrantedAuthoritiesConverter(
            LocalUserIdentityService identityService,
            PermissionRepository permissionRepository
    ) {
        this.identityService = identityService;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Collection<GrantedAuthority> convert(
            Jwt jwt
    ) {
        Optional<Long> userId =
                identityService.resolveAndLinkActiveUserId(
                        jwt
                );

        if (userId.isEmpty()) {
            return List.of();
        }

        return permissionRepository
                .findActivePermissionCodesForUser(
                        userId.get(),
                        Instant.now()
                )
                .stream()
                .map(
                        permission ->
                                (GrantedAuthority)
                                        new SimpleGrantedAuthority(
                                                permission
                                        )
                )
                .toList();
    }
}
