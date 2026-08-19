package ru.practica2026.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import org.springframework.security.config.web.server.ServerHttpSecurity;

import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http
    ) {
        http
                .csrf(
                        ServerHttpSecurity
                                .CsrfSpec
                                ::disable
                )

                .authorizeExchange(
                        exchanges ->
                                exchanges

                                        .pathMatchers(
                                                "/actuator/health",
                                                "/actuator/health/**",
                                                "/actuator/info",
                                                "/actuator/prometheus"
                                        )
                                        .permitAll()

                                        .pathMatchers(
                                                "/actuator/gateway/**"
                                        )
                                        .authenticated()

                                        .pathMatchers(
                                                "/api/**"
                                        )
                                        .authenticated()

                                        .anyExchange()
                                        .denyAll()
                )

                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2.jwt(
                                        Customizer.withDefaults()
                                )
                );

        return http.build();
    }
}
