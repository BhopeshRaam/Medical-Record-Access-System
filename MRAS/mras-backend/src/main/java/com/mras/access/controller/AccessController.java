package com.mras.access.controller;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mras.access.dto.AccessGrantResponse;
import com.mras.access.dto.GrantAccessRequest;
import com.mras.access.dto.RevokeAccessRequest;
import com.mras.access.service.AccessGrantService;
import com.mras.auth.jwt.MrasUserPrincipal;
import com.mras.common.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/access")
public class AccessController {

    private final AccessGrantService service;

    public AccessController(AccessGrantService service) {
        this.service = service;
    }

    // Patient (self) OR ADMIN grants staff access
    @PostMapping("/grant")
    @PreAuthorize("hasAnyRole('ADMIN','PATIENT')")
    public ResponseEntity<ApiResponse<AccessGrantResponse>> grant(
            @Valid @RequestBody GrantAccessRequest body,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) {
        var g = service.grant(body.getPatientId(), body.getStaffUserId(), body.getReason(), principal, httpReq);
        return ResponseEntity.ok(ApiResponse.ok("Access granted", AccessGrantResponse.from(g)));
    }

    // Patient (self) OR ADMIN revoke staff access
    @PostMapping("/revoke")
    @PreAuthorize("hasAnyRole('ADMIN','PATIENT')")
    public ResponseEntity<ApiResponse<AccessGrantResponse>> revoke(
            @Valid @RequestBody RevokeAccessRequest body,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) {
        var g = service.revoke(body.getPatientId(), body.getStaffUserId(), body.getReason(), principal, httpReq);
        return ResponseEntity.ok(ApiResponse.ok("Access revoked", AccessGrantResponse.from(g)));
    }

    // List active grants for a patient (ADMIN or that PATIENT)
    @GetMapping("/patient")
    @PreAuthorize("hasAnyRole('ADMIN','PATIENT')")
    public ResponseEntity<ApiResponse<java.util.List<AccessGrantResponse>>> listForPatient(
            @RequestParam String patientId,
            @AuthenticationPrincipal MrasUserPrincipal principal
    ) {
        var grants = service.listActiveByPatient(patientId, principal)
                .stream().map(AccessGrantResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok("Active grants", grants));
    }

    // List active grants for the logged-in staff (DOCTOR/NURSE)
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('DOCTOR','NURSE')")
    public ResponseEntity<ApiResponse<java.util.List<AccessGrantResponse>>> listForMe(
            @AuthenticationPrincipal MrasUserPrincipal principal
    ) {
        var grants = service.listActiveForMeAsStaff(principal.getId(), principal)
                .stream().map(AccessGrantResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok("My active grants", grants));
    }
}
