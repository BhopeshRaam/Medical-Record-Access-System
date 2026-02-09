package com.mras.records.dto;

import java.time.Instant;
import java.util.List;

import com.mras.records.model.Prescription;
import com.mras.records.model.TestResult;
import com.mras.records.model.Vitals;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public class RecordCreateRequest {

    private Instant encounterDate; // optional

    @Size(max = 500)
    private String chiefComplaint;

    private List<String> diagnosis;

    @Valid
    private Vitals vitals;

    @Valid
    private List<Prescription> prescriptions;

    @Valid
    private List<TestResult> tests;

    @Size(max = 10000)
    private String notes;

    public Instant getEncounterDate() { return encounterDate; }
    public void setEncounterDate(Instant encounterDate) { this.encounterDate = encounterDate; }

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
}
