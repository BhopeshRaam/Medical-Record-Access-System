package com.mras.access.dto;

import jakarta.validation.constraints.NotBlank;

public class RevokeAccessRequest {

    @NotBlank
    private String patientId;

    @NotBlank
    private String staffUserId;

    private String reason;

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getStaffUserId() { return staffUserId; }
    public void setStaffUserId(String staffUserId) { this.staffUserId = staffUserId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
