package com.mras.audit.controller;

import java.time.Instant;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mras.audit.dto.AuditLogResponse;
import com.mras.audit.service.AuditSearchService;
import com.mras.common.dto.ApiResponse;
import com.mras.common.dto.PageResponse;
import com.mras.common.enums.AuditAction;
import com.mras.common.enums.ResourceType;

@RestController
@RequestMapping("/api/admin/audit")
public class AdminAuditController {

    private final AuditSearchService auditSearchService;

    public AdminAuditController(AuditSearchService auditSearchService) {
        this.auditSearchService = auditSearchService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> search(
            @RequestParam(required = false) String actorId,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) ResourceType resourceType,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) String status,

            // ISO-8601 instants: 2026-02-07T10:00:00Z
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp,desc") String sort
    ) {
        String[] parts = sort.split(",");
        String sortField = parts[0];
        String sortDir = (parts.length > 1) ? parts[1] : "desc";

        var pageable = PageRequest.of(
                page,
                Math.min(size, 100),
                "asc".equalsIgnoreCase(sortDir)
                        ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

        var resultPage = auditSearchService.search(
                actorId, action, resourceType, resourceId, status, from, to, pageable
        );

        var items = resultPage.getContent().stream().map(AuditLogResponse::from).toList();
        var payload = PageResponse.from(resultPage, items);

        return ResponseEntity.ok(ApiResponse.ok("Audit logs", payload));
    }
}
