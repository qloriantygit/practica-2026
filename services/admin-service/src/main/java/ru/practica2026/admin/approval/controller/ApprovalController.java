package ru.practica2026.admin.approval.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.admin.approval.dto.request.ApprovalDecisionRequest;
import ru.practica2026.admin.approval.dto.response.ApprovalPageResponse;
import ru.practica2026.admin.approval.dto.response.ApprovalResponse;
import ru.practica2026.admin.approval.entity.ApprovalResourceType;
import ru.practica2026.admin.approval.entity.ApprovalStatus;
import ru.practica2026.admin.approval.service.ApprovalRequestService;
import ru.practica2026.admin.approval.service.ApprovalWorkflowService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/approvals")
public class ApprovalController {

    private final ApprovalRequestService requestService;
    private final ApprovalWorkflowService workflowService;

    public ApprovalController(
            ApprovalRequestService requestService,
            ApprovalWorkflowService workflowService
    ) {
        this.requestService = requestService;
        this.workflowService = workflowService;
    }

    @GetMapping
    public ApprovalPageResponse findAll(
            @RequestParam(required = false)
            ApprovalStatus status,

            @RequestParam(required = false)
            ApprovalResourceType resourceType,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        return requestService.findAll(
                status,
                resourceType,
                page,
                size
        );
    }

    @GetMapping("/{businessKey}")
    public ApprovalResponse findOne(
            @PathVariable
            UUID businessKey
    ) {
        return requestService.findOne(
                businessKey
        );
    }

    @PostMapping("/{businessKey}/approve")
    public ApprovalResponse approve(
            @PathVariable
            UUID businessKey,

            @RequestBody(required = false)
            ApprovalDecisionRequest request
    ) {
        return workflowService.approve(
                businessKey,
                request
        );
    }

    @PostMapping("/{businessKey}/reject")
    public ApprovalResponse reject(
            @PathVariable
            UUID businessKey,

            @RequestBody(required = false)
            ApprovalDecisionRequest request
    ) {
        return workflowService.reject(
                businessKey,
                request
        );
    }
}
