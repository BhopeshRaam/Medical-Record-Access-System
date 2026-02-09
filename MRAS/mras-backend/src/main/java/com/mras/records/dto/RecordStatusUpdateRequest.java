package com.mras.records.dto;

import com.mras.common.enums.RecordStatus;

import jakarta.validation.constraints.NotNull;

public class RecordStatusUpdateRequest {

    @NotNull
    private RecordStatus status;

    private String reason;

    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
