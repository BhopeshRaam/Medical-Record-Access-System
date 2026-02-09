package com.mras.patients.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mras.audit.service.AuditService;
import com.mras.auth.jwt.MrasUserPrincipal;
import com.mras.common.dto.ApiResponse;
import com.mras.common.enums.AuditAction;
import com.mras.common.enums.ResourceType;
import com.mras.common.enums.Role;
import com.mras.patients.dto.PatientCreateRequest;
import com.mras.patients.dto.PatientResponse;
import com.mras.patients.dto.PatientStatusUpdateRequest;
import com.mras.patients.dto.PatientUpdateRequest;
import com.mras.patients.service.PatientService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

	private final PatientService patientService;
	private final AuditService auditService;

	public PatientController(PatientService patientService, AuditService auditService) {
		this.patientService = patientService;
		this.auditService = auditService;
	}

	// Create patient: ADMIN or RECEPTIONIST
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
	public ResponseEntity<ApiResponse<PatientResponse>> create(@Valid @RequestBody PatientCreateRequest reqBody,
			@AuthenticationPrincipal MrasUserPrincipal principal, HttpServletRequest httpReq) {
		var p = patientService.create(reqBody, principal.getId());
		auditService.logSuccess(principal, AuditAction.CREATE_PATIENT, ResourceType.PATIENT, p.getId(), httpReq);
		return ResponseEntity.ok(ApiResponse.ok("Patient created", PatientResponse.from(p)));
	}

	// Search/list: ADMIN/RECEPTIONIST/DOCTOR/NURSE
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE')")
	public ResponseEntity<ApiResponse<com.mras.common.dto.PageResponse<PatientResponse>>> search(
			@RequestParam(required = false) String q, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(defaultValue = "false") boolean includeArchived,
			@AuthenticationPrincipal MrasUserPrincipal principal, HttpServletRequest httpReq) {
		String[] parts = sort.split(",");
		String sortField = parts[0];
		String sortDir = (parts.length > 1) ? parts[1] : "desc";

		var pageable = org.springframework.data.domain.PageRequest.of(page, Math.min(size, 50),
				"asc".equalsIgnoreCase(sortDir) ? org.springframework.data.domain.Sort.by(sortField).ascending()
						: org.springframework.data.domain.Sort.by(sortField).descending());

		if (includeArchived && (principal == null || principal.getRoles() == null || !principal.getRoles().contains(Role.ADMIN))) {
            throw new AccessDeniedException("includeArchived is admin-only");
        }

        var resultPage = patientService.search(q, pageable, includeArchived);
		var items = resultPage.getContent().stream().map(PatientResponse::from).toList();
		var payload = com.mras.common.dto.PageResponse.from(resultPage, items);

		// For searches, resourceId is null (not a single patient)
		auditService.logSuccess(principal, AuditAction.VIEW_PATIENT, ResourceType.PATIENT, null, httpReq);

		return ResponseEntity.ok(ApiResponse.ok("Patients", payload));
	}

	// Get by id: ADMIN/RECEPTIONIST/DOCTOR/NURSE
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE')")
	public ResponseEntity<ApiResponse<PatientResponse>> getById(@PathVariable String id,
			@AuthenticationPrincipal MrasUserPrincipal principal, HttpServletRequest httpReq) {
		var p = patientService.getById(id);
		auditService.logSuccess(principal, AuditAction.VIEW_PATIENT, ResourceType.PATIENT, id, httpReq);
		return ResponseEntity.ok(ApiResponse.ok("Patient", PatientResponse.from(p)));
	}

	// Update: ADMIN or RECEPTIONIST
	@PatchMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
	public ResponseEntity<ApiResponse<PatientResponse>> update(@PathVariable String id,
			@Valid @RequestBody PatientUpdateRequest reqBody, @AuthenticationPrincipal MrasUserPrincipal principal,
			HttpServletRequest httpReq) {
		var p = patientService.update(id, reqBody);
		auditService.logSuccess(principal, AuditAction.UPDATE_PATIENT, ResourceType.PATIENT, id, httpReq);
		return ResponseEntity.ok(ApiResponse.ok("Patient updated", PatientResponse.from(p)));
	}

	// Soft delete / lifecycle update (ARCHIVED/INACTIVE/...) - ADMIN or RECEPTIONIST
	@PatchMapping("/{id}/status")
	@PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
	public ResponseEntity<ApiResponse<PatientResponse>> updateStatus(
	        @PathVariable String id,
	        @Valid @RequestBody PatientStatusUpdateRequest body,
	        @AuthenticationPrincipal MrasUserPrincipal principal,
	        HttpServletRequest httpReq
	) {
	    var p = patientService.updateStatus(id, body.getStatus(), principal.getId(), body.getReason());
	    auditService.logSuccess(principal, AuditAction.UPDATE_PATIENT_STATUS, ResourceType.PATIENT, id, httpReq);
	    return ResponseEntity.ok(ApiResponse.ok("Patient status updated", PatientResponse.from(p)));
	}
	
	@GetMapping("/me")
	@PreAuthorize("hasRole('PATIENT')")
	public ResponseEntity<ApiResponse<PatientResponse>> me(
	        @AuthenticationPrincipal MrasUserPrincipal principal,
	        HttpServletRequest httpReq
	) {
	    var p = patientService.getByLinkedUserId(principal.getId());

	    // audit: patient viewing own profile
	    auditService.logSuccess(principal, AuditAction.VIEW_PATIENT, ResourceType.PATIENT, p.getId(), httpReq);

	    return ResponseEntity.ok(ApiResponse.ok("My patient profile", PatientResponse.from(p)));
	}

}
