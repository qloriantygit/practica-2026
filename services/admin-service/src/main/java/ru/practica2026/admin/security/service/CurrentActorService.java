package ru.practica2026.admin.security.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentActorService {

    private static final String SYSTEM_ACTOR = "SYSTEM";

    public String getCurrentActor() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                authentication == null
                ||
                !authentication.isAuthenticated()
                ||
                authentication
                        instanceof AnonymousAuthenticationToken
        ) {
            return SYSTEM_ACTOR;
        }

        String actor = authentication.getName();

        if (
                actor == null
                ||
                actor.isBlank()
        ) {
            return SYSTEM_ACTOR;
        }

        return actor;
    }
}
