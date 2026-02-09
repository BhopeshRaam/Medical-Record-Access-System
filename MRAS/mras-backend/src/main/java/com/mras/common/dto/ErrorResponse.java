package com.mras.common.dto;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {
    private boolean success = false;
    private String message;
    private String path;
    private int status;
    private Instant timestamp = Instant.now();
    private List<FieldViolation> violations; // for validation errors (optional)

    public ErrorResponse(String message, String path, int status) {
        this.message = message;
        this.path = path;
        this.status = status;
    }

    public ErrorResponse(String message, String path, int status, List<FieldViolation> violations) {
        this.message = message;
        this.path = path;
        this.status = status;
        this.violations = violations;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public int getStatus() { return status; }
    public Instant getTimestamp() { return timestamp; }
    public List<FieldViolation> getViolations() { return violations; }

    public static class FieldViolation {
        private String field;
        private String error;

        public FieldViolation(String field, String error) {
            this.field = field;
            this.error = error;
        }

        public String getField() { return field; }
        public String getError() { return error; }
    }
}
