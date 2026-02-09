package com.mras.audit.dto;

import java.time.Instant;
import java.util.Set;

import com.mras.audit.model.AuditLog;
import com.mras.common.enums.AuditAction;
import com.mras.common.enums.ResourceType;
import com.mras.common.enums.Role;

public class AuditLogResponse {

    private String id;
    private String actorId;
    private Set<Role> actorRoles;

    private AuditAction action;

    private ResourceType resourceType;
    private String resourceId;

    private Instant timestamp;

    private String ip;
    private String userAgent;

    private String status;
    private String reason;

    public static AuditLogResponse from(AuditLog a) {
        AuditLogResponse r = new AuditLogResponse();
        r.id = a.getId();
        r.actorId = a.getActorId();
        r.actorRoles = a.getActorRoles();
        r.action = a.getAction();
        r.resourceType = a.getResourceType();
        r.resourceId = a.getResourceId();
        r.timestamp = a.getTimestamp();
        r.ip = a.getIp();
        r.userAgent = a.getUserAgent();
        r.status = a.getStatus();
        r.reason = a.getReason();
        return r;
    }

    public String getId() { return id; }
    public String getActorId() { return actorId; }
    public Set<Role> getActorRoles() { return actorRoles; }
    public AuditAction getAction() { return action; }
    public ResourceType getResourceType() { return resourceType; }
    public String getResourceId() { return resourceId; }
    public Instant getTimestamp() { return timestamp; }
    public String getIp() { return ip; }
    public String getUserAgent() { return userAgent; }
    public String getStatus() { return status; }
    public String getReason() { return reason; }
}
