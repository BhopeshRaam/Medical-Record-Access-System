package com.mras.records.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mras.common.enums.RecordStatus;

@Document(collection = "records")
public class Record {

    @Id
    private String id;

    @Indexed
    private String patientId;

    @Indexed
    private Instant encounterDate = Instant.now();

    @Indexed
    private String authorId; // doctor userId

    private String chiefComplaint;

    private List<String> diagnosis = new ArrayList<>();

    private Vitals vitals;

    private List<Prescription> prescriptions = new ArrayList<>();

    private List<TestResult> tests = new ArrayList<>();

    private String notes;

    private List<Attachment> attachments = new ArrayList<>();

    private String visibility = "NORMAL"; // NORMAL / RESTRICTED (optional)

    private RecordStatus status = RecordStatus.ACTIVE;
    private Instant voidedAt;
    private String voidedBy;
    private String voidedReason;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public void touchUpdatedAt() { this.updatedAt = Instant.now(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public Instant getEncounterDate() { return encounterDate; }
    public void setEncounterDate(Instant encounterDate) { this.encounterDate = encounterDate; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }

    public List<String> getDiagnosis() { return diagnosis; }
    public void setDiagnosis(List<String> diagnosis) { this.diagnosis = diagnosis; }

    public Vitals getVitals() { return vitals; }
    public void setVitals(Vitals vitals) { this.vitals = vitals; }

    public List<Prescription> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(List<Prescription> prescriptions) { this.prescriptions = prescriptions; }

    public List<TestResult> getTests() { return tests; }
    public void setTests(List<TestResult> tests) { this.tests = tests; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<Attachment> getAttachments() { return attachments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }

    public Instant getVoidedAt() { return voidedAt; }
    public void setVoidedAt(Instant voidedAt) { this.voidedAt = voidedAt; }

    public String getVoidedBy() { return voidedBy; }
    public void setVoidedBy(String voidedBy) { this.voidedBy = voidedBy; }

    public String getVoidedReason() { return voidedReason; }
    public void setVoidedReason(String voidedReason) { this.voidedReason = voidedReason; }
}
