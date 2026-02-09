package com.mras.records.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.mras.access.service.AccessGrantService;
import com.mras.audit.service.AuditService;
import com.mras.auth.jwt.MrasUserPrincipal;
import com.mras.common.enums.AuditAction;
import com.mras.common.enums.RecordStatus;
import com.mras.common.enums.ResourceType;
import com.mras.common.enums.Role;
import com.mras.common.exception.NotFoundException;
import com.mras.patients.service.PatientService;
import com.mras.records.dto.RecordCreateRequest;
import com.mras.records.dto.RecordUpdateRequest;
import com.mras.records.model.Record;
import com.mras.records.repo.RecordRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class RecordService {

    private final RecordRepository recordRepo;
    private final PatientService patientService;
    private final AuditService auditService;
    private final AccessGrantService accessGrantService;
    private final RecordPdfService recordPdfService;

    public RecordService(
            RecordRepository recordRepo,
            PatientService patientService,
            AuditService auditService,
            AccessGrantService accessGrantService,
            RecordPdfService recordPdfService
    ) {
        this.recordRepo = recordRepo;
        this.patientService = patientService;
        this.auditService = auditService;
        this.accessGrantService = accessGrantService;
        this.recordPdfService = recordPdfService;
    }

    public Record createForPatient(
            String patientId,
            RecordCreateRequest req,
            MrasUserPrincipal actor,
            HttpServletRequest httpReq
    ) {
        if (!hasAny(actor, Role.DOCTOR, Role.ADMIN)) {
            auditService.logDenied(actor, AuditAction.CREATE_RECORD, ResourceType.RECORD, null, httpReq, "Not allowed");
            throw new AccessDeniedException("Not allowed");
        }

        patientService.getById(patientId);

        Record r = new Record();
        r.setPatientId(patientId);
        r.setAuthorId(actor.getId());

        if (req.getEncounterDate() != null) r.setEncounterDate(req.getEncounterDate());
        r.setChiefComplaint(req.getChiefComplaint());
        if (req.getDiagnosis() != null) r.setDiagnosis(req.getDiagnosis());
        r.setVitals(req.getVitals());
        if (req.getPrescriptions() != null) r.setPrescriptions(req.getPrescriptions());
        if (req.getTests() != null) r.setTests(req.getTests());
        r.setNotes(req.getNotes());
        r.setStatus(RecordStatus.ACTIVE);

        Record saved = recordRepo.save(r);
        auditService.logSuccess(actor, AuditAction.CREATE_RECORD, ResourceType.RECORD, saved.getId(), httpReq);
        return saved;
    }

    public Page<Record> listByPatient(
            String patientId,
            Pageable pageable,
            boolean includeArchived,
            MrasUserPrincipal actor,
            HttpServletRequest httpReq
    ) {
        if (has(actor, Role.PATIENT)) {
            String myPatientId = patientService.getByLinkedUserId(actor.getId()).getId();
            if (!patientId.equals(myPatientId)) {
                auditService.logDenied(actor, AuditAction.VIEW_RECORD, ResourceType.PATIENT, patientId, httpReq, "Patient mismatch");
                throw new AccessDeniedException("Not allowed");
            }
        } else {
            if (!hasAny(actor, Role.ADMIN, Role.DOCTOR, Role.NURSE)) {
                auditService.logDenied(actor, AuditAction.VIEW_RECORD, ResourceType.PATIENT, patientId, httpReq, "Not allowed");
                throw new AccessDeniedException("Not allowed");
            }

            if (hasAny(actor, Role.DOCTOR, Role.NURSE) && !has(actor, Role.ADMIN)) {
                boolean granted = accessGrantService.hasActiveGrant(patientId, actor.getId());
                if (!granted) {
                    auditService.logDenied(actor, AuditAction.VIEW_RECORD, ResourceType.PATIENT, patientId, httpReq, "No access grant");
                    throw new AccessDeniedException("No access grant for this patient");
                }
            }
        }

        patientService.getById(patientId);

        Page<Record> page;
        if (includeArchived && has(actor, Role.ADMIN)) {
            page = recordRepo.findByPatientId(patientId, pageable);
        } else {
            page = recordRepo.findByPatientIdAndStatusNotIn(
                    patientId,
                    java.util.List.of(RecordStatus.VOIDED, RecordStatus.ARCHIVED),
                    pageable
            );
        }

        auditService.logSuccess(actor, AuditAction.VIEW_RECORD, ResourceType.PATIENT, patientId, httpReq);
        return page;
    }

    public Record getById(String recordId, MrasUserPrincipal actor, HttpServletRequest httpReq) {
        Record r = recordRepo.findById(recordId).orElseThrow(() -> new NotFoundException("Record not found"));
        if (r.getStatus() == RecordStatus.VOIDED || r.getStatus() == RecordStatus.ARCHIVED) {
            throw new NotFoundException("Record not found");
        }

        if (has(actor, Role.PATIENT)) {
            String myPatientId = patientService.getByLinkedUserId(actor.getId()).getId();
            if (!r.getPatientId().equals(myPatientId)) {
                auditService.logDenied(actor, AuditAction.VIEW_RECORD, ResourceType.RECORD, recordId, httpReq, "Patient mismatch");
                throw new AccessDeniedException("Not allowed");
            }
        } else {
            if (!hasAny(actor, Role.ADMIN, Role.DOCTOR, Role.NURSE)) {
                auditService.logDenied(actor, AuditAction.VIEW_RECORD, ResourceType.RECORD, recordId, httpReq, "Not allowed");
                throw new AccessDeniedException("Not allowed");
            }

            if (hasAny(actor, Role.DOCTOR, Role.NURSE) && !has(actor, Role.ADMIN)) {
                boolean granted = accessGrantService.hasActiveGrant(r.getPatientId(), actor.getId());
                boolean isAuthor = actor.getId() != null && actor.getId().equals(r.getAuthorId());
                if (!granted && !isAuthor) {
                    auditService.logDenied(actor, AuditAction.VIEW_RECORD, ResourceType.RECORD, recordId, httpReq, "No access grant");
                    throw new AccessDeniedException("No access grant for this patient");
                }
            }
        }

        auditService.logSuccess(actor, AuditAction.VIEW_RECORD, ResourceType.RECORD, recordId, httpReq);
        return r;
    }

    public byte[] exportRecordPdf(String recordId, MrasUserPrincipal actor, HttpServletRequest httpReq) {
        Record r = recordRepo.findById(recordId).orElseThrow(() -> new NotFoundException("Record not found"));
        if (r.getStatus() == RecordStatus.VOIDED || r.getStatus() == RecordStatus.ARCHIVED) {
            throw new NotFoundException("Record not found");
        }

        if (has(actor, Role.PATIENT)) {
            String myPatientId = patientService.getByLinkedUserId(actor.getId()).getId();
            if (!r.getPatientId().equals(myPatientId)) {
                auditService.logDenied(actor, AuditAction.EXPORT_RECORD_PDF, ResourceType.RECORD, recordId, httpReq, "Patient mismatch");
                throw new AccessDeniedException("Not allowed");
            }
        } else {
            if (!hasAny(actor, Role.ADMIN, Role.DOCTOR, Role.NURSE)) {
                auditService.logDenied(actor, AuditAction.EXPORT_RECORD_PDF, ResourceType.RECORD, recordId, httpReq, "Not allowed");
                throw new AccessDeniedException("Not allowed");
            }

            if (hasAny(actor, Role.DOCTOR, Role.NURSE) && !has(actor, Role.ADMIN)) {
                boolean granted = accessGrantService.hasActiveGrant(r.getPatientId(), actor.getId());
                boolean isAuthor = actor.getId() != null && actor.getId().equals(r.getAuthorId());
                if (!granted && !isAuthor) {
                    auditService.logDenied(actor, AuditAction.EXPORT_RECORD_PDF, ResourceType.RECORD, recordId, httpReq, "No access grant");
                    throw new AccessDeniedException("No access grant for this patient");
                }
            }
        }

        var patient = patientService.getById(r.getPatientId());
        byte[] pdf = recordPdfService.buildRecordPdf(r, patient);

        auditService.logSuccess(actor, AuditAction.EXPORT_RECORD_PDF, ResourceType.RECORD, recordId, httpReq);
        return pdf;
    }

    public Record update(String recordId, RecordUpdateRequest req, MrasUserPrincipal actor, HttpServletRequest httpReq) {
        Record r = recordRepo.findById(recordId).orElseThrow(() -> new NotFoundException("Record not found"));
        if (r.getStatus() == RecordStatus.VOIDED || r.getStatus() == RecordStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot update a voided/archived record");
        }

        if (!canUpdateRecord(r, actor)) {
            auditService.logDenied(actor, AuditAction.UPDATE_RECORD, ResourceType.RECORD, recordId, httpReq, "Not allowed");
            throw new AccessDeniedException("Not allowed");
        }

        if (req.getChiefComplaint() != null) r.setChiefComplaint(req.getChiefComplaint());
        if (req.getDiagnosis() != null) r.setDiagnosis(req.getDiagnosis());
        if (req.getVitals() != null) r.setVitals(req.getVitals());
        if (req.getPrescriptions() != null) r.setPrescriptions(req.getPrescriptions());
        if (req.getTests() != null) r.setTests(req.getTests());
        if (req.getNotes() != null) r.setNotes(req.getNotes());
        if (req.getVisibility() != null) r.setVisibility(req.getVisibility());

        r.touchUpdatedAt();
        Record saved = recordRepo.save(r);
        auditService.logSuccess(actor, AuditAction.UPDATE_RECORD, ResourceType.RECORD, recordId, httpReq);
        return saved;
    }

    public Record updateStatus(
            String recordId,
            RecordStatus status,
            String reason,
            MrasUserPrincipal actor,
            HttpServletRequest httpReq
    ) {
        Record r = recordRepo.findById(recordId).orElseThrow(() -> new NotFoundException("Record not found"));

        if (!has(actor, Role.ADMIN) && !(has(actor, Role.DOCTOR) && actor.getId() != null && actor.getId().equals(r.getAuthorId()))) {
            auditService.logDenied(actor, AuditAction.UPDATE_RECORD_STATUS, ResourceType.RECORD, recordId, httpReq, "Not allowed");
            throw new AccessDeniedException("Not allowed");
        }

        r.setStatus(status);

        if (status == RecordStatus.VOIDED || status == RecordStatus.ARCHIVED) {
            r.setVoidedAt(java.time.Instant.now());
            r.setVoidedBy(actor.getId());
            r.setVoidedReason(reason);
        } else {
            r.setVoidedAt(null);
            r.setVoidedBy(null);
            r.setVoidedReason(null);
        }

        r.touchUpdatedAt();
        Record saved = recordRepo.save(r);

        auditService.logSuccess(actor, AuditAction.UPDATE_RECORD_STATUS, ResourceType.RECORD, recordId, httpReq);
        return saved;
    }

    public boolean canViewRecord(Record r, MrasUserPrincipal actor) {
        if (actor == null) return false;

        if (has(actor, Role.PATIENT)) {
            String myPatientId = patientService.getByLinkedUserId(actor.getId()).getId();
            return r.getPatientId() != null && r.getPatientId().equals(myPatientId);
        }

        if (has(actor, Role.ADMIN)) return true;

        if (hasAny(actor, Role.DOCTOR, Role.NURSE)) {
            boolean granted = accessGrantService.hasActiveGrant(r.getPatientId(), actor.getId());
            boolean isAuthor = actor.getId() != null && actor.getId().equals(r.getAuthorId());
            return granted || isAuthor;
        }

        return false;
    }

    public boolean canUpdateRecord(Record r, MrasUserPrincipal actor) {
        if (actor == null) return false;
        if (has(actor, Role.ADMIN)) return true;
        return has(actor, Role.DOCTOR) && actor.getId() != null && actor.getId().equals(r.getAuthorId());
    }

    private boolean has(MrasUserPrincipal p, Role role) {
        return p != null && p.getRoles() != null && p.getRoles().contains(role);
    }

    private boolean hasAny(MrasUserPrincipal p, Role... roles) {
        if (p == null || p.getRoles() == null) return false;
        for (Role r : roles) {
            if (p.getRoles().contains(r)) return true;
        }
        return false;
    }
}
