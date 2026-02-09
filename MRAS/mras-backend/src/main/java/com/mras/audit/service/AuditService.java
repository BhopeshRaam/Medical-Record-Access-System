package com.mras.audit.service;

import org.springframework.stereotype.Service;

import com.mras.audit.model.AuditLog;
import com.mras.audit.repo.AuditLogRepository;
import com.mras.auth.jwt.MrasUserPrincipal;
import com.mras.common.enums.AuditAction;
import com.mras.common.enums.ResourceType;

import jakarta.servlet.http.HttpServletRequest;

import static com.mras.common.util.RequestUtil.getClientIp;
import static com.mras.common.util.RequestUtil.getUserAgent;

@Service
public class AuditService {

    private final AuditLogRepository auditRepo;

    public AuditService(AuditLogRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    public void logSuccess(MrasUserPrincipal actor, AuditAction action, ResourceType resourceType,
                           String resourceId, HttpServletRequest req) {
        save(actor, action, resourceType, resourceId, req, "SUCCESS", null);
    }

    public void logDenied(MrasUserPrincipal actor, AuditAction action, ResourceType resourceType,
                          String resourceId, HttpServletRequest req, String reason) {
        save(actor, action, resourceType, resourceId, req, "DENIED", reason);
    }

    public void logError(MrasUserPrincipal actor, AuditAction action, ResourceType resourceType,
                         String resourceId, HttpServletRequest req, String reason) {
        save(actor, action, resourceType, resourceId, req, "ERROR", reason);
    }

    private void save(MrasUserPrincipal actor, AuditAction action, ResourceType resourceType,
                      String resourceId, HttpServletRequest req, String status, String reason) {
        AuditLog log = new AuditLog();
        log.setActorId(actor != null ? actor.getId() : null);
        log.setActorRoles(actor != null ? actor.getRoles() : null);
        log.setAction(action);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setIp(req != null ? getClientIp(req) : null);
        log.setUserAgent(req != null ? getUserAgent(req) : null);
        log.setStatus(status);
        log.setReason(reason);

        auditRepo.save(log);
    }
    
    public void logLoginSuccess(com.mras.users.model.User user, jakarta.servlet.http.HttpServletRequest req) {
        com.mras.audit.model.AuditLog log = new com.mras.audit.model.AuditLog();
        log.setActorId(user.getId());
        log.setActorRoles(user.getRoles());
        log.setAction(com.mras.common.enums.AuditAction.LOGIN);
        log.setResourceType(com.mras.common.enums.ResourceType.USER);
        log.setResourceId(user.getId());
        log.setIp(req != null ? com.mras.common.util.RequestUtil.getClientIp(req) : null);
        log.setUserAgent(req != null ? com.mras.common.util.RequestUtil.getUserAgent(req) : null);
        log.setStatus("SUCCESS");
        auditRepo.save(log);
    }

    public void logLoginDenied(String email, jakarta.servlet.http.HttpServletRequest req, String reason) {
        com.mras.audit.model.AuditLog log = new com.mras.audit.model.AuditLog();
        // actorId unknown on failed login; store email in reason so it's searchable
        log.setActorId(null);
        log.setActorRoles(null);
        log.setAction(com.mras.common.enums.AuditAction.LOGIN);
        log.setResourceType(com.mras.common.enums.ResourceType.USER);
        log.setResourceId(null);
        log.setIp(req != null ? com.mras.common.util.RequestUtil.getClientIp(req) : null);
        log.setUserAgent(req != null ? com.mras.common.util.RequestUtil.getUserAgent(req) : null);
        log.setStatus("DENIED");
        log.setReason("email=" + (email == null ? "null" : email) + "; " + reason);
        auditRepo.save(log);
    }
    
    public void logRegisterSuccess(com.mras.users.model.User user, jakarta.servlet.http.HttpServletRequest req) {
        com.mras.audit.model.AuditLog log = new com.mras.audit.model.AuditLog();
        log.setActorId(user.getId());
        log.setActorRoles(user.getRoles());
        log.setAction(com.mras.common.enums.AuditAction.REGISTER);
        log.setResourceType(com.mras.common.enums.ResourceType.USER);
        log.setResourceId(user.getId());
        log.setIp(req != null ? com.mras.common.util.RequestUtil.getClientIp(req) : null);
        log.setUserAgent(req != null ? com.mras.common.util.RequestUtil.getUserAgent(req) : null);
        log.setStatus("SUCCESS");
        auditRepo.save(log);
    }

    public void logRegisterDenied(String email, jakarta.servlet.http.HttpServletRequest req, String reason) {
        com.mras.audit.model.AuditLog log = new com.mras.audit.model.AuditLog();
        log.setActorId(null);
        log.setActorRoles(null);
        log.setAction(com.mras.common.enums.AuditAction.REGISTER);
        log.setResourceType(com.mras.common.enums.ResourceType.USER);
        log.setResourceId(null);
        log.setIp(req != null ? com.mras.common.util.RequestUtil.getClientIp(req) : null);
        log.setUserAgent(req != null ? com.mras.common.util.RequestUtil.getUserAgent(req) : null);
        log.setStatus("DENIED");
        log.setReason("email=" + (email == null ? "null" : email) + "; " + reason);
        auditRepo.save(log);
    }


}
