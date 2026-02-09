package com.mras.access.dto;

import java.time.Instant;

import com.mras.access.model.AccessGrant;
import com.mras.common.enums.AccessGrantStatus;

public class AccessGrantResponse {

    private String id;
    private String patientId;
    private String staffUserId;
    private AccessGrantStatus status;
    private String grantedBy;
    private Instant grantedAt;
    private String revokedBy;
    private Instant revokedAt;

    public static AccessGrantResponse from(AccessGrant g) {
        AccessGrantResponse r = new AccessGrantResponse();
        r.id = g.getId();
        r.patientId = g.getPatientId();
        r.staffUserId = g.getGranteeUserId();
        r.status = g.getStatus();
        r.grantedBy = g.getGrantedBy();
        r.grantedAt = g.getGrantedAt();
        r.revokedBy = g.getRevokedBy();
        r.revokedAt = g.getRevokedAt();
        return r;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getStaffUserId() { return staffUserId; }
    public AccessGrantStatus getStatus() { return status; }
    public String getGrantedBy() { return grantedBy; }
    public Instant getGrantedAt() { return grantedAt; }
    public String getRevokedBy() { return revokedBy; }
    public Instant getRevokedAt() { return revokedAt; }
}
