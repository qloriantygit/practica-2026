package ru.practica2026.admin.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter
        extends OncePerRequestFilter {

    public static final String HEADER =
            "X-Correlation-Id";

    public static final String ATTRIBUTE =
            "correlationId";

    private static final Logger log =
            LoggerFactory.getLogger(
                    CorrelationIdFilter.class
            );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String correlationId =
                request.getHeader(HEADER);

        if (
                correlationId == null
                ||
                correlationId.isBlank()
        ) {
            correlationId =
                    UUID.randomUUID().toString();
        }

        request.setAttribute(
                ATTRIBUTE,
                correlationId
        );

        response.setHeader(
                HEADER,
                correlationId
        );

        MDC.put(
                ATTRIBUTE,
                correlationId
        );

        try {
            log.info(
                    "HTTP request method={} path={} correlationId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    correlationId
            );

            filterChain.doFilter(
                    request,
                    response
            );
        }
        finally {
            MDC.remove(ATTRIBUTE);
        }
    }
}
