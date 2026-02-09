package com.mras.audit.model;

import java.time.Instant;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mras.common.enums.AuditAction;
import com.mras.common.enums.ResourceType;
import com.mras.common.enums.Role;

@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    @Indexed
    private String actorId;

    private Set<Role> actorRoles;

    @Indexed
    private AuditAction action;

    private ResourceType resourceType;

    @Indexed
    private String resourceId;

    @Indexed
    private Instant timestamp = Instant.now();

    private String ip;
    private String userAgent;

    private String status; // SUCCESS / DENIED / ERROR
    private String reason; // optional

    public AuditLog() {}

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public Set<Role> getActorRoles() { return actorRoles; }
    public void setActorRoles(Set<Role> actorRoles) { this.actorRoles = actorRoles; }

    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }

    public ResourceType getResourceType() { return resourceType; }
    public void setResourceType(ResourceType resourceType) { this.resourceType = resourceType; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
