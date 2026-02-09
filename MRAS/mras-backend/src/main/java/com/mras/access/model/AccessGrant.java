package com.mras.access.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mras.common.enums.AccessGrantStatus;

@Document(collection = "access_grants")
public class AccessGrant {

    @Id
    private String id;

    @Indexed
    private String patientId;

    @Indexed
    private String granteeUserId; // doctor/nurse userId

    private AccessGrantStatus status = AccessGrantStatus.ACTIVE;

    private String grantedBy; // patient userId (or admin)
    private Instant grantedAt = Instant.now();

    private String revokedBy;
    private Instant revokedAt;
    private String revokedReason;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getGranteeUserId() { return granteeUserId; }
    public void setGranteeUserId(String granteeUserId) { this.granteeUserId = granteeUserId; }

    public AccessGrantStatus getStatus() { return status; }
    public void setStatus(AccessGrantStatus status) { this.status = status; }

    public String getGrantedBy() { return grantedBy; }
    public void setGrantedBy(String grantedBy) { this.grantedBy = grantedBy; }

    public Instant getGrantedAt() { return grantedAt; }
    public void setGrantedAt(Instant grantedAt) { this.grantedAt = grantedAt; }

    public String getRevokedBy() { return revokedBy; }
    public void setRevokedBy(String revokedBy) { this.revokedBy = revokedBy; }

    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }

    public String getRevokedReason() { return revokedReason; }
    public void setRevokedReason(String revokedReason) { this.revokedReason = revokedReason; }
}
