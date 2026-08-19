package ru.practica2026.admin.outbox.service;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Service
public class CurrentCorrelationIdService {

    public static final String HEADER =
            "X-Correlation-Id";

    public String getCurrentCorrelationId() {

        if (
                RequestContextHolder.getRequestAttributes()
                        instanceof ServletRequestAttributes attributes
        ) {
            HttpServletRequest request =
                    attributes.getRequest();

            String correlationId =
                    request.getHeader(HEADER);

            if (
                    correlationId != null
                    &&
                    !correlationId.isBlank()
            ) {
                return correlationId.trim();
            }
        }

        return UUID.randomUUID()
                .toString();
    }
}
