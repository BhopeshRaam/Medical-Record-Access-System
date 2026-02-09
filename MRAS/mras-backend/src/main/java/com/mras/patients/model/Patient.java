package com.mras.patients.model;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mras.common.enums.PatientStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document(collection = "patients")
public class Patient {

    @Id
    private String id;

    @NotBlank
    @Indexed(unique = true)
    private String mrn; // Medical Record Number

    @NotBlank
    @Size(min = 2, max = 120)
    private String name;

    @NotNull
    private LocalDate dob;

    @NotBlank
    private String gender; // "M","F","O","U" (we keep simple for now)

    @NotBlank
    @Size(min = 6, max = 20)
    private String phone;

    private Address address;

    // If patient has their own login, link here (optional)
    private String linkedUserId; // user _id (String)

    // Soft delete / lifecycle
    @NotNull
    private PatientStatus status = PatientStatus.ACTIVE;

    private Instant deletedAt;
    private String deletedBy; // userId
    private String deletedReason;

    @NotNull
    private String createdBy; // user _id (String)

    @NotNull
    private Instant createdAt = Instant.now();

    @NotNull
    private Instant updatedAt = Instant.now();

    public void touchUpdatedAt() { this.updatedAt = Instant.now(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMrn() { return mrn; }
    public void setMrn(String mrn) { this.mrn = mrn; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public String getLinkedUserId() { return linkedUserId; }
    public void setLinkedUserId(String linkedUserId) { this.linkedUserId = linkedUserId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public PatientStatus getStatus() { return status; }
    public void setStatus(PatientStatus status) { this.status = status; }

    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }

    public String getDeletedBy() { return deletedBy; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }

    public String getDeletedReason() { return deletedReason; }
    public void setDeletedReason(String deletedReason) { this.deletedReason = deletedReason; }
}
