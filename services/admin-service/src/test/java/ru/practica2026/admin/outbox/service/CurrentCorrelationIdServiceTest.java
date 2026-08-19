package ru.practica2026.admin.outbox.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CurrentCorrelationIdServiceTest {

    private final CurrentCorrelationIdService service =
            new CurrentCorrelationIdService();

    @AfterEach
    void cleanup() {
        RequestContextHolder
                .resetRequestAttributes();
    }

    @Test
    void returnsCorrelationIdFromRequestHeader() {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                CurrentCorrelationIdService.HEADER,
                "test-correlation-123"
        );

        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(
                        request
                )
        );

        String result =
                service.getCurrentCorrelationId();

        assertEquals(
                "test-correlation-123",
                result
        );
    }

    @Test
    void generatesCorrelationIdWhenHeaderIsMissing() {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(
                        request
                )
        );

        String result =
                service.getCurrentCorrelationId();

        assertNotNull(result);

        UUID.fromString(result);
    }
}
