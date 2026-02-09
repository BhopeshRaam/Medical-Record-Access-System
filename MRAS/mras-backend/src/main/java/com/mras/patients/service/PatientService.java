package com.mras.patients.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mras.common.enums.PatientStatus;
import com.mras.common.exception.NotFoundException;
import com.mras.patients.dto.PatientCreateRequest;
import com.mras.patients.dto.PatientUpdateRequest;
import com.mras.patients.model.Patient;
import com.mras.patients.repo.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepo;

    public PatientService(PatientRepository patientRepo) {
        this.patientRepo = patientRepo;
    }

    public Patient create(PatientCreateRequest req, String createdByUserId) {
        String mrn = req.getMrn().trim();
        if (patientRepo.existsByMrn(mrn)) {
            throw new IllegalArgumentException("MRN already exists");
        }

        Patient p = new Patient();
        p.setMrn(mrn);
        p.setName(req.getName().trim());
        p.setDob(req.getDob());
        p.setGender(req.getGender().trim());
        p.setPhone(req.getPhone().trim());
        p.setAddress(req.getAddress());

        if (req.getLinkedUserId() != null && !req.getLinkedUserId().trim().isEmpty()) {
            String link = req.getLinkedUserId().trim();
            if (patientRepo.existsByLinkedUserId(link)) {
                throw new IllegalArgumentException("linkedUserId is already mapped to another patient");
            }
            p.setLinkedUserId(link);
        } else {
            p.setLinkedUserId(null);
        }

        p.setCreatedBy(createdByUserId);
        p.setStatus(PatientStatus.ACTIVE);
        return patientRepo.save(p);
    }

    public Patient getById(String id) {
        Patient p = patientRepo.findById(id).orElseThrow(() -> new NotFoundException("Patient not found"));
        if (p.getStatus() == PatientStatus.ARCHIVED) {
            throw new NotFoundException("Patient not found");
        }
        return p;
    }

    public Page<Patient> search(String q, Pageable pageable, boolean includeArchived) {
        String qq = (q == null) ? null : q.trim();
        boolean blank = (qq == null || qq.isEmpty());

        if (includeArchived) {
            if (blank) return patientRepo.findAll(pageable);
            return patientRepo.searchAll(qq, pageable);
        }

        if (blank) return patientRepo.findByStatusNot(PatientStatus.ARCHIVED, pageable);
        return patientRepo.search(qq, pageable);
    }

    public Patient update(String id, PatientUpdateRequest req) {
        Patient p = getById(id);

        if (req.getName() != null) p.setName(req.getName().trim());
        if (req.getDob() != null) p.setDob(req.getDob());
        if (req.getGender() != null) p.setGender(req.getGender().trim());
        if (req.getPhone() != null) p.setPhone(req.getPhone().trim());
        if (req.getAddress() != null) p.setAddress(req.getAddress());

        if (req.getLinkedUserId() != null) {
            String newLink = req.getLinkedUserId().trim();
            if (newLink.isEmpty()) {
                p.setLinkedUserId(null);
            } else {
                if (!newLink.equals(p.getLinkedUserId()) && patientRepo.existsByLinkedUserId(newLink)) {
                    throw new IllegalArgumentException("linkedUserId is already mapped to another patient");
                }
                p.setLinkedUserId(newLink);
            }
        }

        p.touchUpdatedAt();
        return patientRepo.save(p);
    }

    public Patient getByLinkedUserId(String userId) {
        return patientRepo.findByLinkedUserIdAndStatusNot(userId, PatientStatus.ARCHIVED)
                .orElseThrow(() -> new NotFoundException("Patient profile not linked to this user"));
    }

    public Patient updateStatus(String id, PatientStatus status, String actorUserId, String reason) {
        Patient p = patientRepo.findById(id).orElseThrow(() -> new NotFoundException("Patient not found"));
        p.setStatus(status);

        if (status == PatientStatus.ARCHIVED) {
            p.setDeletedAt(java.time.Instant.now());
            p.setDeletedBy(actorUserId);
            p.setDeletedReason(reason);
        } else {
            p.setDeletedAt(null);
            p.setDeletedBy(null);
            p.setDeletedReason(null);
        }

        p.touchUpdatedAt();
        return patientRepo.save(p);
    }
}
