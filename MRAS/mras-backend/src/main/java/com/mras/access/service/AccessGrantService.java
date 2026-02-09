package com.mras.access.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mras.access.model.AccessGrant;
import com.mras.access.repo.AccessGrantRepository;
import com.mras.audit.service.AuditService;
import com.mras.auth.jwt.MrasUserPrincipal;
import com.mras.common.enums.AccessGrantStatus;
import com.mras.common.enums.AuditAction;
import com.mras.common.enums.ResourceType;
import com.mras.common.enums.Role;
import com.mras.common.exception.NotFoundException;
import com.mras.patients.service.PatientService;
import com.mras.users.repo.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AccessGrantService {

    private final AccessGrantRepository repo;
    private final PatientService patientService;
    private final UserRepository userRepo;
    private final AuditService auditService;

    public AccessGrantService(AccessGrantRepository repo, PatientService patientService, UserRepository userRepo, AuditService auditService) {
        this.repo = repo;
        this.patientService = patientService;
        this.userRepo = userRepo;
        this.auditService = auditService;
    }

    public boolean hasActiveGrant(String patientId, String staffUserId) {
        if (patientId == null || staffUserId == null) return false;
        return repo.findByPatientIdAndGranteeUserIdAndStatus(patientId, staffUserId, AccessGrantStatus.ACTIVE).isPresent();
    }

    public AccessGrant grant(String patientId, String staffUserId, String reason, MrasUserPrincipal actor, HttpServletRequest httpReq) {
        // ensure patient exists + not archived
        patientService.getById(patientId);

        var staff = userRepo.findById(staffUserId).orElseThrow(() -> new NotFoundException("Staff user not found"));
        boolean isDoctorOrNurse = staff.getRoles() != null && (staff.getRoles().contains(Role.DOCTOR) || staff.getRoles().contains(Role.NURSE));
        if (!isDoctorOrNurse) {
            auditService.logDenied(actor, AuditAction.GRANT_ACCESS, ResourceType.ACCESS_GRANT, null, httpReq, "Target is not doctor/nurse");
            throw new IllegalArgumentException("Access can only be granted to DOCTOR/NURSE");
        }

        // who can grant?
        // - ADMIN can grant for anyone
        // - PATIENT can grant for self only
        if (actor.getRoles() == null) {
            auditService.logDenied(actor, AuditAction.GRANT_ACCESS, ResourceType.ACCESS_GRANT, null, httpReq, "No roles");
            throw new org.springframework.security.access.AccessDeniedException("Not allowed");
        }
        if (actor.getRoles().contains(Role.PATIENT)) {
            String myPatientId = patientService.getByLinkedUserId(actor.getId()).getId();
            if (!patientId.equals(myPatientId)) {
                auditService.logDenied(actor, AuditAction.GRANT_ACCESS, ResourceType.ACCESS_GRANT, null, httpReq, "Patient mismatch");
                throw new org.springframework.security.access.AccessDeniedException("Not allowed");
            }
        } else if (!actor.getRoles().contains(Role.ADMIN)) {
            auditService.logDenied(actor, AuditAction.GRANT_ACCESS, ResourceType.ACCESS_GRANT, null, httpReq, "Not allowed");
            throw new org.springframework.security.access.AccessDeniedException("Not allowed");
        }

        // if already active, return existing
        var existing = repo.findByPatientIdAndGranteeUserIdAndStatus(patientId, staffUserId, AccessGrantStatus.ACTIVE);
        if (existing.isPresent()) {
            auditService.logSuccess(actor, AuditAction.GRANT_ACCESS, ResourceType.ACCESS_GRANT, existing.get().getId(), httpReq);
            return existing.get();
        }

        AccessGrant g = new AccessGrant();
        g.setPatientId(patientId);
        g.setGranteeUserId(staffUserId);
        g.setStatus(AccessGrantStatus.ACTIVE);
        g.setGrantedBy(actor.getId());
        g.setGrantedAt(Instant.now());
        // store reason in revokedReason? we keep it simple -> ignore or could add field; we'll reuse revokedReason is wrong.

        AccessGrant saved = repo.save(g);
        auditService.logSuccess(actor, AuditAction.GRANT_ACCESS, ResourceType.ACCESS_GRANT, saved.getId(), httpReq);
        return saved;
    }

    public AccessGrant revoke(String patientId, String staffUserId, String reason, MrasUserPrincipal actor, HttpServletRequest httpReq) {
        // Same permission rules as grant
        if (actor.getRoles().contains(Role.PATIENT)) {
            String myPatientId = patientService.getByLinkedUserId(actor.getId()).getId();
            if (!patientId.equals(myPatientId)) {
                auditService.logDenied(actor, AuditAction.REVOKE_ACCESS, ResourceType.ACCESS_GRANT, null, httpReq, "Patient mismatch");
                throw new org.springframework.security.access.AccessDeniedException("Not allowed");
            }
        } else if (!actor.getRoles().contains(Role.ADMIN)) {
            auditService.logDenied(actor, AuditAction.REVOKE_ACCESS, ResourceType.ACCESS_GRANT, null, httpReq, "Not allowed");
            throw new org.springframework.security.access.AccessDeniedException("Not allowed");
        }

        AccessGrant g = repo.findByPatientIdAndGranteeUserIdAndStatus(patientId, staffUserId, AccessGrantStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Active grant not found"));

        g.setStatus(AccessGrantStatus.REVOKED);
        g.setRevokedAt(Instant.now());
        g.setRevokedBy(actor.getId());
        g.setRevokedReason(reason);

        AccessGrant saved = repo.save(g);
        auditService.logSuccess(actor, AuditAction.REVOKE_ACCESS, ResourceType.ACCESS_GRANT, saved.getId(), httpReq);
        return saved;
    }

    public List<AccessGrant> listActiveByPatient(String patientId, MrasUserPrincipal actor) {
        // patient can view their own; admin can view any
        if (actor.getRoles().contains(Role.PATIENT)) {
            String myPatientId = patientService.getByLinkedUserId(actor.getId()).getId();
            if (!patientId.equals(myPatientId)) throw new org.springframework.security.access.AccessDeniedException("Not allowed");
        } else if (!actor.getRoles().contains(Role.ADMIN)) {
            throw new org.springframework.security.access.AccessDeniedException("Not allowed");
        }
        return repo.findByPatientIdAndStatus(patientId, AccessGrantStatus.ACTIVE);
    }

    public List<AccessGrant> listActiveForMeAsStaff(String staffUserId, MrasUserPrincipal actor) {
        if (!actor.getId().equals(staffUserId)) throw new org.springframework.security.access.AccessDeniedException("Not allowed");
        return repo.findByGranteeUserIdAndStatus(staffUserId, AccessGrantStatus.ACTIVE);
    }
}
