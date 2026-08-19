package ru.practica2026.admin.approval.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practica2026.admin.approval.entity.ApprovalRequest;
import ru.practica2026.admin.approval.entity.ApprovalResourceType;
import ru.practica2026.admin.approval.entity.ApprovalStatus;

import java.util.Optional;
import java.util.UUID;

public interface ApprovalRequestRepository
        extends JpaRepository<ApprovalRequest, Long> {

    Optional<ApprovalRequest> findByBusinessKey(
            UUID businessKey
    );

    boolean existsByResourceTypeAndResourceKeyAndStatus(
            ApprovalResourceType resourceType,
            UUID resourceKey,
            ApprovalStatus status
    );

    Page<ApprovalRequest> findAllByStatus(
            ApprovalStatus status,
            Pageable pageable
    );

    Page<ApprovalRequest> findAllByResourceType(
            ApprovalResourceType resourceType,
            Pageable pageable
    );

    Page<ApprovalRequest> findAllByStatusAndResourceType(
            ApprovalStatus status,
            ApprovalResourceType resourceType,
            Pageable pageable
    );
}
