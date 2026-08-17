package ru.practica2026.admin.security.config;


import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import ru.practica2026.admin.security.converter.LocalPermissionJwtGrantedAuthoritiesConverter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            LocalPermissionJwtGrantedAuthoritiesConverter authoritiesConverter
    ) throws Exception {

        JwtAuthenticationConverter jwtAuthenticationConverter =
                new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setPrincipalClaimName(
                "preferred_username"
        );

        jwtAuthenticationConverter
                .setJwtGrantedAuthoritiesConverter(
                        authoritiesConverter
                );

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )

                .authorizeHttpRequests(
                        authorization -> authorization
                                .dispatcherTypeMatchers(
                                        DispatcherType.ERROR
                                )
                                .permitAll()

                                .requestMatchers(
                                        "/actuator/health",
                                        "/actuator/info",
                                        "/actuator/prometheus"
                                )
                                .permitAll()

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/v1/organizations",
                                        "/api/v1/organizations/**"
                                )
                                .hasAuthority(
                                        "ORGANIZATION_READ"
                                )

                                .requestMatchers(
                                        "/api/v1/organizations",
                                        "/api/v1/organizations/**"
                                )
                                .hasAuthority(
                                        "ORGANIZATION_MANAGE"
                                )

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/v1/users/*/roles/*"
                                )
                                .hasAuthority(
                                        "ROLE_MANAGE"
                                )

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/v1/users/*/roles/*"
                                )
                                .hasAuthority(
                                        "ROLE_MANAGE"
                                )

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/v1/users",
                                        "/api/v1/users/**"
                                )
                                .hasAuthority(
                                        "USER_READ"
                                )

                                .requestMatchers(
                                        "/api/v1/users",
                                        "/api/v1/users/**"
                                )
                                .hasAuthority(
                                        "USER_MANAGE"
                                )

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/v1/roles",
                                        "/api/v1/roles/**"
                                )
                                .hasAuthority(
                                        "ROLE_READ"
                                )

                                .requestMatchers(
                                        "/api/v1/roles",
                                        "/api/v1/roles/**"
                                )
                                .hasAuthority(
                                        "ROLE_MANAGE"
                                )

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/v1/permissions",
                                        "/api/v1/permissions/**"
                                )
                                .hasAuthority(
                                        "ROLE_READ"
                                )

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/v1/directories",
                                        "/api/v1/directories/**",
                                        "/api/v1/directory-versions",
                                        "/api/v1/directory-versions/**"
                                )
                                .hasAuthority(
                                        "DIRECTORY_READ"
                                )
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/v1/directory-versions/*/publish"
                                )
                                .hasAuthority(
                                        "DIRECTORY_PUBLISH"
                                )


                                .requestMatchers(
                                        "/api/v1/directories",
                                        "/api/v1/directories/**",
                                        "/api/v1/directory-versions",
                                        "/api/v1/directory-versions/**"
                                )
                                .hasAuthority(
                                        "DIRECTORY_MANAGE"
                                )

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/v1/experts",
                                        "/api/v1/experts/**"
                                )
                                .hasAuthority(
                                        "EXPERT_READ"
                                )

                                .requestMatchers(
                                        "/api/v1/experts",
                                        "/api/v1/experts/**"
                                )
                                .hasAuthority(
                                        "EXPERT_MANAGE"
                                )

                                .requestMatchers(
                                        "/api/**"
                                )
                                .denyAll()

                                .anyRequest()
                                .denyAll()
                )

                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2.jwt(
                                        jwt ->
                                                jwt.jwtAuthenticationConverter(
                                                        jwtAuthenticationConverter
                                                )
                                )
                );

        return http.build();
    }
}
