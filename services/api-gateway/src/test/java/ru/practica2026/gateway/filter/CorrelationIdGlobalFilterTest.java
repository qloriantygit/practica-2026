package ru.practica2026.gateway.filter;

import org.junit.jupiter.api.Test;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;

import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CorrelationIdGlobalFilterTest {

    private static final String HEADER =
            "X-Correlation-Id";

    private final CorrelationIdGlobalFilter filter =
            new CorrelationIdGlobalFilter();

    @Test
    void existingCorrelationIdIsPreserved() {
        MockServerWebExchange exchange =
                MockServerWebExchange.from(
                        MockServerHttpRequest
                                .get("/api/v1/test")
                                .header(
                                        HEADER,
                                        "existing-correlation-id"
                                )
                                .build()
                );

        AtomicReference<String> receivedCorrelationId =
                new AtomicReference<>();

        GatewayFilterChain chain =
                filteredExchange -> {
                    receivedCorrelationId.set(
                            filteredExchange
                                    .getRequest()
                                    .getHeaders()
                                    .getFirst(HEADER)
                    );

                    return Mono.empty();
                };

        filter.filter(
                exchange,
                chain
        ).block();

        assertEquals(
                "existing-correlation-id",
                receivedCorrelationId.get()
        );
    }

    @Test
    void correlationIdIsGeneratedWhenHeaderIsMissing() {
        MockServerWebExchange exchange =
                MockServerWebExchange.from(
                        MockServerHttpRequest
                                .get("/api/v1/test")
                                .build()
                );

        AtomicReference<String> receivedCorrelationId =
                new AtomicReference<>();

        GatewayFilterChain chain =
                filteredExchange -> {
                    receivedCorrelationId.set(
                            filteredExchange
                                    .getRequest()
                                    .getHeaders()
                                    .getFirst(HEADER)
                    );

                    return Mono.empty();
                };

        filter.filter(
                exchange,
                chain
        ).block();

        String correlationId =
                receivedCorrelationId.get();

        assertNotNull(
                correlationId
        );

        UUID.fromString(
                correlationId
        );
    }
}
