package com.mras.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mras.audit.service.AuditService;
import com.mras.auth.dto.LoginRequest;
import com.mras.auth.dto.LoginResponse;
import com.mras.auth.dto.RegisterRequest;
import com.mras.auth.dto.UserPublicDto;
import com.mras.auth.jwt.MrasUserPrincipal;
import com.mras.auth.service.AuthService;
import com.mras.auth.service.MeService;
import com.mras.common.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;
    private final MeService meService;
    private final AuditService auditService;

    public AuthController(AuthService authService, MeService meService, AuditService auditService) {
        this.authService = authService;
        this.meService = meService;
        this.auditService = auditService;
    }


    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserPublicDto>> register(
            @Valid @RequestBody RegisterRequest req,
            jakarta.servlet.http.HttpServletRequest httpReq
    ) {
        try {
            var user = authService.register(req);
            auditService.logRegisterSuccess(user, httpReq);

            var dto = new UserPublicDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRoles()
            );

            return ResponseEntity.ok(ApiResponse.ok("User registered", dto));
        } catch (IllegalArgumentException ex) {
            auditService.logRegisterDenied(req.getEmail(), httpReq, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            auditService.logRegisterDenied(req.getEmail(), httpReq, "Unexpected error");
            throw ex;
        }
    }



    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest httpReq
    ) {
        try {
            var resp = authService.login(req);

            // We need the User object to log actorId/roles; simplest: fetch by email
            // (AuthService already did it; but it doesn't expose it.)
            // We'll do a light fetch here.
            var user = authService.getUserByEmailForAudit(resp.getEmail());
            auditService.logLoginSuccess(user, httpReq);

            return ResponseEntity.ok(ApiResponse.ok("Login successful", resp));
        } catch (IllegalArgumentException ex) {
            auditService.logLoginDenied(req.getEmail(), httpReq, ex.getMessage());
            throw ex; // let GlobalExceptionHandler format response
        } catch (Exception ex) {
            auditService.logLoginDenied(req.getEmail(), httpReq, "Unexpected error");
            throw ex;
        }
    }


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(@AuthenticationPrincipal MrasUserPrincipal principal) {
        // principal is set by JwtAuthFilter
        var me = meService.getMeByEmail(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Me", me));
    }
}
