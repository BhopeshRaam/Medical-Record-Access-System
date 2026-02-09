package com.mras.records.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.mras.auth.jwt.MrasUserPrincipal;
import com.mras.common.dto.ApiResponse;
import com.mras.common.dto.PageResponse;
import com.mras.common.enums.Role;
import com.mras.records.dto.RecordCreateRequest;
import com.mras.records.dto.RecordResponse;
import com.mras.records.dto.RecordStatusUpdateRequest;
import com.mras.records.dto.RecordUpdateRequest;
import com.mras.records.service.RecordService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    // Create record for a patient (DOCTOR/ADMIN)
    @PostMapping("/api/patients/{patientId}/records")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<ApiResponse<RecordResponse>> create(
            @PathVariable String patientId,
            @Valid @RequestBody RecordCreateRequest body,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) {
        var r = recordService.createForPatient(patientId, body, principal, httpReq);
        return ResponseEntity.ok(ApiResponse.ok("Record created", RecordResponse.from(r)));
    }

    // List records for a patient:
    // - STAFF: ADMIN/DOCTOR/NURSE
    // - PATIENT: only their own patientId (checked in service)
    @GetMapping("/api/patients/{patientId}/records")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','PATIENT')")
    public ResponseEntity<ApiResponse<PageResponse<RecordResponse>>> list(
            @PathVariable String patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "encounterDate,desc") String sort,
            @RequestParam(defaultValue = "false") boolean includeArchived,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) {
        String[] parts = sort.split(",");
        String sortField = parts[0];
        String sortDir = (parts.length > 1) ? parts[1] : "desc";

        var pageable = PageRequest.of(
                page,
                Math.min(size, 50),
                "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending()
        );

        if (includeArchived && (principal == null || principal.getRoles() == null || !principal.getRoles().contains(Role.ADMIN))) {
            throw new AccessDeniedException("includeArchived is admin-only");
        }

        var resultPage = recordService.listByPatient(patientId, pageable, includeArchived, principal, httpReq);
        var items = resultPage.getContent().stream().map(RecordResponse::from).toList();
        var payload = PageResponse.from(resultPage, items);

        return ResponseEntity.ok(ApiResponse.ok("Records", payload));
    }

    // Get record by id (same access rules)
    @GetMapping("/api/records/{recordId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','PATIENT')")
    public ResponseEntity<ApiResponse<RecordResponse>> getById(
            @PathVariable String recordId,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) {
        var r = recordService.getById(recordId, principal, httpReq);
        return ResponseEntity.ok(ApiResponse.ok("Record", RecordResponse.from(r)));
    }

    

// Export record as PDF (download)
@GetMapping("/api/records/{recordId}/pdf")
@PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','PATIENT')")
public org.springframework.http.ResponseEntity<byte[]> exportPdf(
        @PathVariable String recordId,
        @AuthenticationPrincipal MrasUserPrincipal principal,
        HttpServletRequest httpReq
) {
    byte[] pdf = recordService.exportRecordPdf(recordId, principal, httpReq);
    String filename = "record-" + recordId + ".pdf";
    return org.springframework.http.ResponseEntity.ok()
            .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
            .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(pdf);
}

// Update record (ADMIN or author DOCTOR)
    @PatchMapping("/api/records/{recordId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<ApiResponse<RecordResponse>> update(
            @PathVariable String recordId,
            @Valid @RequestBody RecordUpdateRequest body,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) {
        var r = recordService.update(recordId, body, principal, httpReq);
        return ResponseEntity.ok(ApiResponse.ok("Record updated", RecordResponse.from(r)));
    }

    // Soft delete / lifecycle update (VOIDED/ARCHIVED/...) - ADMIN or author DOCTOR
    @PatchMapping("/api/records/{recordId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<ApiResponse<RecordResponse>> updateStatus(
            @PathVariable String recordId,
            @Valid @RequestBody RecordStatusUpdateRequest body,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) {
        var r = recordService.updateStatus(recordId, body.getStatus(), body.getReason(), principal, httpReq);
        return ResponseEntity.ok(ApiResponse.ok("Record status updated", RecordResponse.from(r)));
    }
}
