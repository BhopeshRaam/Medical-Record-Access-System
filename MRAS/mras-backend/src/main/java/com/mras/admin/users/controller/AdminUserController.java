package com.mras.admin.users.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mras.admin.users.dto.AdminUserResponse;
import com.mras.admin.users.dto.AdminUserUpdateRequest;
import com.mras.admin.users.dto.ResetPasswordRequest;
import com.mras.admin.users.service.AdminUserService;
import com.mras.audit.service.AuditService;
import com.mras.auth.jwt.MrasUserPrincipal;
import com.mras.common.dto.ApiResponse;
import com.mras.common.dto.PageResponse;
import com.mras.common.enums.AuditAction;
import com.mras.common.enums.ResourceType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService userService;
    private final AuditService auditService;

    public AdminUserController(AdminUserService userService, AuditService auditService) {
        this.userService = userService;
        this.auditService = auditService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminUserResponse>>> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
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

        var result = userService.search(q, pageable);
        var items = result.getContent().stream().map(AdminUserResponse::from).toList();
        var payload = PageResponse.from(result, items);
        auditService.logSuccess(principal, AuditAction.VIEW_USER, ResourceType.USER, null, httpReq);
        return ResponseEntity.ok(ApiResponse.ok("Users", payload));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getById(
            @PathVariable String id,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) {
        var u = userService.getById(id);
        auditService.logSuccess(principal, AuditAction.VIEW_USER, ResourceType.USER, id, httpReq);
        return ResponseEntity.ok(ApiResponse.ok("User", AdminUserResponse.from(u)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody AdminUserUpdateRequest body,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) {
        var u = userService.update(id, body);
        auditService.logSuccess(principal, AuditAction.UPDATE_USER, ResourceType.USER, id, httpReq);
        return ResponseEntity.ok(ApiResponse.ok("User updated", AdminUserResponse.from(u)));
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable String id,
            @Valid @RequestBody ResetPasswordRequest body,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) {
        userService.resetPassword(id, body.getNewPassword());
        auditService.logSuccess(principal, AuditAction.RESET_PASSWORD, ResourceType.USER, id, httpReq);
        return ResponseEntity.ok(ApiResponse.ok("Password reset", null));
    }
}
