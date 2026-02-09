package com.mras.files.service;

import java.io.IOException;
import java.util.Iterator;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mras.audit.service.AuditService;
import com.mras.auth.jwt.MrasUserPrincipal;
import com.mras.common.enums.AuditAction;
import com.mras.common.enums.ResourceType;
import com.mras.common.exception.NotFoundException;
import com.mras.records.model.Attachment;
import com.mras.records.model.Record;
import com.mras.records.repo.RecordRepository;
import com.mras.records.service.RecordService;
import com.mongodb.client.gridfs.model.GridFSFile;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class GridFsService {

    private final GridFsTemplate gridFsTemplate;
    private final RecordRepository recordRepo;
    private final RecordService recordService;
    private final AuditService auditService;

    public GridFsService(
            GridFsTemplate gridFsTemplate,
            RecordRepository recordRepo,
            RecordService recordService,
            AuditService auditService
    ) {
        this.gridFsTemplate = gridFsTemplate;
        this.recordRepo = recordRepo;
        this.recordService = recordService;
        this.auditService = auditService;
    }

    public Attachment uploadToRecord(String recordId, MultipartFile file, MrasUserPrincipal actor, HttpServletRequest httpReq)
            throws IOException {

        Record r = recordRepo.findById(recordId)
                .orElseThrow(() -> new NotFoundException("Record not found"));

        // Only ADMIN or (author) DOCTOR can upload to a record
        if (!recordService.canUpdateRecord(r, actor)) {
            auditService.logDenied(actor, AuditAction.UPLOAD_FILE, ResourceType.RECORD, recordId, httpReq, "Not allowed");
            throw new AccessDeniedException("Not allowed");
        }

        String filename = (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank())
                ? "upload"
                : file.getOriginalFilename();

        String contentType = (file.getContentType() == null) ? "application/octet-stream" : file.getContentType();

        // Metadata to enforce access on download
        Document metadata = new Document();
        metadata.put("recordId", recordId);
        metadata.put("patientId", r.getPatientId());
        metadata.put("uploaderId", actor.getId());
        metadata.put("filename", filename);
        metadata.put("contentType", contentType);
        metadata.put("sizeBytes", file.getSize());

        ObjectId fileObjectId = gridFsTemplate.store(file.getInputStream(), filename, contentType, metadata);

        Attachment att = new Attachment();
        att.setStorage("GRIDFS");
        att.setFileId(fileObjectId.toHexString());
        att.setFilename(filename);
        att.setMimeType(contentType);
        att.setSizeBytes(file.getSize());

        r.getAttachments().add(att);
        r.touchUpdatedAt();
        recordRepo.save(r);

        auditService.logSuccess(actor, AuditAction.UPLOAD_FILE, ResourceType.FILE, att.getFileId(), httpReq);
        return att;
    }

    public GridFsResource download(String fileId, MrasUserPrincipal actor, HttpServletRequest httpReq) {
        ObjectId oid;
        try {
            oid = new ObjectId(fileId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid fileId");
        }

        GridFSFile gfile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(oid)));
        if (gfile == null) throw new NotFoundException("File not found");

        // Extract recordId from metadata
        Document meta = (gfile.getMetadata() == null) ? new Document() : gfile.getMetadata();
        String recordId = meta.getString("recordId");

        if (recordId == null) {
            // If metadata missing, safest is deny
            auditService.logDenied(actor, AuditAction.DOWNLOAD_FILE, ResourceType.FILE, fileId, httpReq, "Missing recordId metadata");
            throw new AccessDeniedException("Not allowed");
        }

        Record r = recordRepo.findById(recordId)
                .orElseThrow(() -> new NotFoundException("Record not found for this file"));

        if (!recordService.canViewRecord(r, actor)) {
            auditService.logDenied(actor, AuditAction.DOWNLOAD_FILE, ResourceType.FILE, fileId, httpReq, "Not allowed");
            throw new AccessDeniedException("Not allowed");
        }

        auditService.logSuccess(actor, AuditAction.DOWNLOAD_FILE, ResourceType.FILE, fileId, httpReq);

        return gridFsTemplate.getResource(gfile);
    }
    
    public void deleteFromRecord(String recordId, String fileId, MrasUserPrincipal actor, HttpServletRequest httpReq) {
        Record r = recordRepo.findById(recordId)
                .orElseThrow(() -> new NotFoundException("Record not found"));

        // Only ADMIN or author DOCTOR can delete
        if (!recordService.canUpdateRecord(r, actor)) {
            auditService.logDenied(actor, AuditAction.DELETE_FILE, ResourceType.RECORD, recordId, httpReq, "Not allowed");
            throw new AccessDeniedException("Not allowed");
        }

        // Find attachment in record
        boolean removed = false;
        Iterator<com.mras.records.model.Attachment> it = r.getAttachments().iterator();
        while (it.hasNext()) {
            var att = it.next();
            if ("GRIDFS".equalsIgnoreCase(att.getStorage()) && fileId.equals(att.getFileId())) {
                it.remove();
                removed = true;
                break;
            }
        }

        if (!removed) {
            throw new NotFoundException("Attachment not found on this record");
        }

        // Delete GridFS file
        ObjectId oid;
        try {
            oid = new ObjectId(fileId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid fileId");
        }

        gridFsTemplate.delete(new Query(Criteria.where("_id").is(oid)));

        r.touchUpdatedAt();
        recordRepo.save(r);

        auditService.logSuccess(actor, AuditAction.DELETE_FILE, ResourceType.FILE, fileId, httpReq);
    }

}
