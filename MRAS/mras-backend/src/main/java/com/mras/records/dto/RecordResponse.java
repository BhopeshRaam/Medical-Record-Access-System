package com.mras.records.dto;

import java.time.Instant;
import java.util.List;

import com.mras.common.enums.RecordStatus;
import com.mras.records.model.Attachment;
import com.mras.records.model.Record;
import com.mras.records.model.Prescription;
import com.mras.records.model.TestResult;
import com.mras.records.model.Vitals;

public class RecordResponse {
    private String id;
    private String patientId;
    private Instant encounterDate;
    private String authorId;

    private String chiefComplaint;
    private List<String> diagnosis;
    private Vitals vitals;
    private List<Prescription> prescriptions;
    private List<TestResult> tests;
    private String notes;

    private List<Attachment> attachments;
    private String visibility;

    private RecordStatus status;
    private Instant voidedAt;

    private Instant createdAt;
    private Instant updatedAt;

    public static RecordResponse from(Record r) {
        RecordResponse x = new RecordResponse();
        x.id = r.getId();
        x.patientId = r.getPatientId();
        x.encounterDate = r.getEncounterDate();
        x.authorId = r.getAuthorId();
        x.chiefComplaint = r.getChiefComplaint();
        x.diagnosis = r.getDiagnosis();
        x.vitals = r.getVitals();
        x.prescriptions = r.getPrescriptions();
        x.tests = r.getTests();
        x.notes = r.getNotes();
        x.attachments = r.getAttachments();
        x.visibility = r.getVisibility();
        x.status = r.getStatus();
        x.voidedAt = r.getVoidedAt();
        x.createdAt = r.getCreatedAt();
        x.updatedAt = r.getUpdatedAt();
        return x;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public Instant getEncounterDate() { return encounterDate; }
    public String getAuthorId() { return authorId; }
    public String getChiefComplaint() { return chiefComplaint; }
    public List<String> getDiagnosis() { return diagnosis; }
    public Vitals getVitals() { return vitals; }
    public List<Prescription> getPrescriptions() { return prescriptions; }
    public List<TestResult> getTests() { return tests; }
    public String getNotes() { return notes; }
    public List<Attachment> getAttachments() { return attachments; }
    public String getVisibility() { return visibility; }
    public RecordStatus getStatus() { return status; }
    public Instant getVoidedAt() { return voidedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
