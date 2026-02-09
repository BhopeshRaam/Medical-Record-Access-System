package com.mras.patients.dto;

import com.mras.common.enums.PatientStatus;

import jakarta.validation.constraints.NotNull;

public class PatientStatusUpdateRequest {

    @NotNull
    private PatientStatus status;

    // Optional free text (why archived / inactive)
    private String reason;

    public PatientStatus getStatus() {
        return status;
    }

    public void setStatus(PatientStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
