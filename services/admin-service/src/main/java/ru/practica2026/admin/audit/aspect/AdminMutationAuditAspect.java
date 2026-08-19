package ru.practica2026.admin.audit.aspect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import ru.practica2026.admin.audit.service.AdminAuditService;
import ru.practica2026.admin.observability.CorrelationIdFilter;
import ru.practica2026.admin.outbox.event.AdminEventType;
import ru.practica2026.admin.outbox.service.OutboxEventService;
import ru.practica2026.admin.security.service.CurrentActorService;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
public class AdminMutationAuditAspect {

    private static final Pattern UUID_PATTERN =
            Pattern.compile(
                    "[0-9a-fA-F]{8}-"
                            + "[0-9a-fA-F]{4}-"
                            + "[0-9a-fA-F]{4}-"
                            + "[0-9a-fA-F]{4}-"
                            + "[0-9a-fA-F]{12}"
            );

    private final AdminAuditService auditService;
    private final OutboxEventService outboxEventService;
    private final CurrentActorService currentActorService;
    private final ObjectMapper objectMapper;

    private final TransactionTemplate transactionTemplate;

    public AdminMutationAuditAspect(
            AdminAuditService auditService,
            OutboxEventService outboxEventService,
            CurrentActorService currentActorService,
            ObjectMapper objectMapper,
            PlatformTransactionManager transactionManager
    ) {
        this.auditService = auditService;

        this.outboxEventService =
                outboxEventService;

        this.currentActorService =
                currentActorService;

        this.objectMapper =
                objectMapper;

        this.transactionTemplate =
                new TransactionTemplate(
                        transactionManager
                );
    }

    @Around(
            "@within(org.springframework.web.bind.annotation.RestController)"
    )
    public Object audit(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {

        ServletRequestAttributes attributes =
                (
                        ServletRequestAttributes
                )
                        RequestContextHolder
                                .getRequestAttributes();

        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request =
                attributes.getRequest();

        String method =
                request.getMethod();

        if (!isMutation(method)) {
            return joinPoint.proceed();
        }

        String path =
                request.getRequestURI();

        if (!path.startsWith("/api/v1/")) {
            return joinPoint.proceed();
        }

        String correlationId =
                correlationId(request);

        String actor =
                currentActorService
                        .getCurrentActor();

        String entityType =
                resolveEntityType(path);

        String requestState =
                serializeArguments(
                        joinPoint.getArgs()
                );

        AdminEventType eventType =
                resolveEventType(path);

        if (eventType == null) {
            return proceedWithoutBusinessEvent(
                    joinPoint,
                    correlationId,
                    actor,
                    method,
                    path,
                    entityType,
                    requestState
            );
        }

        return proceedWithTransactionalOutbox(
                joinPoint,
                correlationId,
                actor,
                method,
                path,
                entityType,
                requestState,
                eventType
        );
    }

    private Object proceedWithoutBusinessEvent(
            ProceedingJoinPoint joinPoint,
            String correlationId,
            String actor,
            String method,
            String path,
            String entityType,
            String requestState
    ) throws Throwable {

        try {
            Object result =
                    joinPoint.proceed();

            String responseState =
                    safeJson(result);

            String entityKey =
                    resolveEntityKey(
                            result,
                            path
                    );

            auditService.record(
                    correlationId,
                    actor,
                    method,
                    path,
                    entityType,
                    entityKey,
                    requestState,
                    responseState,
                    true,
                    null
            );

            return result;
        }
        catch (Throwable throwable) {

            auditService.record(
                    correlationId,
                    actor,
                    method,
                    path,
                    entityType,
                    extractUuid(path),
                    requestState,
                    null,
                    false,
                    throwable.getMessage()
            );

            throw throwable;
        }
    }

    private Object proceedWithTransactionalOutbox(
            ProceedingJoinPoint joinPoint,
            String correlationId,
            String actor,
            String method,
            String path,
            String entityType,
            String requestState,
            AdminEventType eventType
    ) throws Throwable {

        MutationResult transactionResult;

        try {
            transactionResult =
                    transactionTemplate.execute(
                            status -> {
                                try {
                                    Object result =
                                            joinPoint.proceed();

                                    String responseState =
                                            safeJson(result);

                                    String entityKey =
                                            resolveEntityKey(
                                                    result,
                                                    path
                                            );

                                    outboxEventService.enqueue(
                                            eventType,
                                            correlationId,
                                            actor,
                                            entityKey,
                                            method,
                                            path,
                                            requestState,
                                            responseState
                                    );

                                    return new MutationResult(
                                            result,
                                            responseState,
                                            entityKey
                                    );
                                }
                                catch (Throwable throwable) {
                                    throw new JoinPointExecutionException(
                                            throwable
                                    );
                                }
                            }
                    );
        }
        catch (JoinPointExecutionException exception) {

            Throwable original =
                    exception.getCause();

            auditService.record(
                    correlationId,
                    actor,
                    method,
                    path,
                    entityType,
                    extractUuid(path),
                    requestState,
                    null,
                    false,
                    original == null
                            ? exception.getMessage()
                            : original.getMessage()
            );

            if (original != null) {
                throw original;
            }

            throw exception;
        }
        catch (Throwable throwable) {

            auditService.record(
                    correlationId,
                    actor,
                    method,
                    path,
                    entityType,
                    extractUuid(path),
                    requestState,
                    null,
                    false,
                    throwable.getMessage()
            );

            throw throwable;
        }

        if (transactionResult == null) {
            throw new IllegalStateException(
                    "Transactional mutation returned no result"
            );
        }

        /*
         * TransactionTemplate.execute(...) returns only after
         * the PostgreSQL transaction has been committed.
         *
         * Therefore success audit is written only after both:
         *
         *   business mutation
         *   +
         *   outbox INSERT
         *
         * were successfully committed.
         */
        auditService.record(
                correlationId,
                actor,
                method,
                path,
                entityType,
                transactionResult.entityKey(),
                requestState,
                transactionResult.responseState(),
                true,
                null
        );

        return transactionResult.result();
    }

    private boolean isMutation(
            String method
    ) {
        return "POST".equals(method)
                || "PUT".equals(method)
                || "PATCH".equals(method)
                || "DELETE".equals(method);
    }

    private String correlationId(
            HttpServletRequest request
    ) {
        Object attribute =
                request.getAttribute(
                        CorrelationIdFilter.ATTRIBUTE
                );

        if (attribute != null) {
            return attribute.toString();
        }

        return UUID.randomUUID().toString();
    }

    private String serializeArguments(
            Object[] arguments
    ) {
        try {
            List<Object> safeArguments =
                    new ArrayList<>();

            for (Object argument : arguments) {

                if (
                        argument
                        instanceof MultipartFile file
                ) {
                    safeArguments.add(
                            "MultipartFile{name="
                                    + file.getOriginalFilename()
                                    + ", size="
                                    + file.getSize()
                                    + "}"
                    );
                }
                else {
                    safeArguments.add(argument);
                }
            }

            return objectMapper
                    .writeValueAsString(
                            safeArguments
                    );
        }
        catch (Exception exception) {
            return "\"<request serialization failed>\"";
        }
    }

    private String safeJson(
            Object value
    ) {
        if (value == null) {
            return null;
        }

        try {
            return objectMapper
                    .writeValueAsString(value);
        }
        catch (Exception exception) {
            return "\"<response serialization failed>\"";
        }
    }

    private String resolveEntityKey(
            Object result,
            String path
    ) {
        Object candidate = result;

        if (candidate instanceof ResponseEntity<?> responseEntity) {
            candidate = responseEntity.getBody();
        }

        if (candidate != null) {
            /*
             * First use Jackson because our REST DTOs may be records,
             * ordinary DTOs or wrapped responses. If serialized response
             * contains businessKey, this is the most stable way to read it.
             */
            try {
                JsonNode node =
                        objectMapper.valueToTree(
                                candidate
                        );

                JsonNode businessKey =
                        node.get("businessKey");

                if (
                        businessKey != null
                        &&
                        !businessKey.isNull()
                        &&
                        !businessKey.asText().isBlank()
                ) {
                    return businessKey.asText();
                }
            }
            catch (Exception ignored) {
                // Fall back to reflection.
            }

            /*
             * Java record accessor:
             * response.businessKey()
             */
            try {
                Method method =
                        candidate.getClass()
                                .getMethod(
                                        "businessKey"
                                );

                Object value =
                        method.invoke(candidate);

                if (value != null) {
                    return value.toString();
                }
            }
            catch (Exception ignored) {
                // Try JavaBean getter next.
            }

            /*
             * Ordinary DTO accessor:
             * response.getBusinessKey()
             */
            try {
                Method method =
                        candidate.getClass()
                                .getMethod(
                                        "getBusinessKey"
                                );

                Object value =
                        method.invoke(candidate);

                if (value != null) {
                    return value.toString();
                }
            }
            catch (Exception ignored) {
                // Fall back to UUID from request path.
            }
        }

        return extractUuid(path);
    }

    private String extractUuid(
            String path
    ) {
        Matcher matcher =
                UUID_PATTERN.matcher(path);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    private AdminEventType resolveEventType(
            String path
    ) {
        if (
                path.startsWith(
                        "/api/v1/directories"
                )
                ||
                path.startsWith(
                        "/api/v1/directory-versions"
                )
        ) {
            return AdminEventType
                    .DIRECTORY_UPDATED;
        }

        if (
                path.startsWith(
                        "/api/v1/roles"
                )
                ||
                path.matches(
                        "^/api/v1/users/[^/]+/roles.*"
                )
        ) {
            return AdminEventType
                    .ROLE_CHANGED;
        }

        if (
                path.startsWith(
                        "/api/v1/experts"
                )
        ) {
            return AdminEventType
                    .EXPERT_PROFILE_UPDATED;
        }

        return null;
    }

    private String resolveEntityType(
            String path
    ) {
        if (path.startsWith("/api/v1/approvals")) {
            return "ApprovalRequest";
        }

        if (path.contains("/directory-versions")) {
            return "DirectoryVersion";
        }

        if (path.contains("/directories")) {
            return "Directory";
        }

        if (
                path.startsWith("/api/v1/roles")
                ||
                path.matches(
                        "^/api/v1/users/[^/]+/roles.*"
                )
        ) {
            return "Role";
        }

        if (path.startsWith("/api/v1/experts")) {
            return "ExpertProfile";
        }

        if (path.startsWith("/api/v1/users")) {
            return "UserAccount";
        }

        if (path.startsWith("/api/v1/organizations")) {
            return "Organization";
        }

        if (path.startsWith("/api/v1/calendars")) {
            return "WorkCalendar";
        }

        if (path.startsWith("/api/v1/sla-policies")) {
            return "SlaPolicy";
        }

        if (path.startsWith("/api/v1/templates")) {
            return "Template";
        }

        if (path.startsWith("/api/v1/document-types")) {
            return "DocumentType";
        }

        if (path.startsWith("/api/v1/document-rules")) {
            return "MandatoryDocumentRule";
        }

        return "AdministrativeResource";
    }

    private record MutationResult(

            Object result,

            String responseState,

            String entityKey
    ) {
    }

    private static final class
    JoinPointExecutionException
            extends RuntimeException {

        private JoinPointExecutionException(
                Throwable cause
        ) {
            super(cause);
        }
    }
}
