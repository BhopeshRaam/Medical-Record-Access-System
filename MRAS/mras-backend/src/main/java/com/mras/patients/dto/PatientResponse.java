package com.mras.patients.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.mras.common.enums.PatientStatus;
import com.mras.patients.model.Address;
import com.mras.patients.model.Patient;

public class PatientResponse {
    private String id;
    private String mrn;
    private String name;
    private LocalDate dob;
    private String gender;
    private String phone;
    private Address address;
    private String linkedUserId;
	private PatientStatus status;
	private Instant deletedAt;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;

    public static PatientResponse from(Patient p) {
        PatientResponse r = new PatientResponse();
        r.id = p.getId();
        r.mrn = p.getMrn();
        r.name = p.getName();
        r.dob = p.getDob();
        r.gender = p.getGender();
        r.phone = p.getPhone();
        r.address = p.getAddress();
        r.linkedUserId = p.getLinkedUserId();
		r.status = p.getStatus();
		r.deletedAt = p.getDeletedAt();
        r.createdBy = p.getCreatedBy();
        r.createdAt = p.getCreatedAt();
        r.updatedAt = p.getUpdatedAt();
        return r;
    }

    public String getId() { return id; }
    public String getMrn() { return mrn; }
    public String getName() { return name; }
    public LocalDate getDob() { return dob; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    public Address getAddress() { return address; }
    public String getLinkedUserId() { return linkedUserId; }
	public PatientStatus getStatus() { return status; }
	public Instant getDeletedAt() { return deletedAt; }
    public String getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
