package ru.practica2026.admin.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ru.practica2026.admin.security.service.OrganizationAccessService;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OrganizationAbacFilter
        extends OncePerRequestFilter {

    private static final Pattern ORGANIZATION_RESOURCE =
            Pattern.compile(
                    "^/api/v1/organizations/([0-9a-fA-F-]{36})(?:/.*)?$"
            );

    private final OrganizationAccessService accessService;

    public OrganizationAbacFilter(
            OrganizationAccessService accessService
    ) {
        this.accessService = accessService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Matcher matcher =
                ORGANIZATION_RESOURCE.matcher(
                        request.getRequestURI()
                );

        if (!matcher.matches()) {
            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        UUID organizationBusinessKey;

        try {
            organizationBusinessKey =
                    UUID.fromString(
                            matcher.group(1)
                    );
        }
        catch (IllegalArgumentException exception) {
            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        try {
            accessService.requireAccess(
                    organizationBusinessKey
            );
        }
        catch (AccessDeniedException exception) {
            response.sendError(
                    HttpStatus.FORBIDDEN.value(),
                    exception.getMessage()
            );

            return;
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}
