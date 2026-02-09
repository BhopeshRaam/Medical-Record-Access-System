package com.mras.files.controller;

import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mras.auth.jwt.MrasUserPrincipal;
import com.mras.common.dto.ApiResponse;
import com.mras.files.service.GridFsService;
import com.mras.records.model.Attachment;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class FileController {

    private final GridFsService gridFsService;

    public FileController(GridFsService gridFsService) {
        this.gridFsService = gridFsService;
    }

    // Upload a file to a record (Doctor/Admin only)
    @PostMapping(value = "/api/records/{recordId}/files", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<ApiResponse<Attachment>> upload(
            @PathVariable String recordId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }
        Attachment att = gridFsService.uploadToRecord(recordId, file, principal, httpReq);
        return ResponseEntity.ok(ApiResponse.ok("File uploaded", att));
    }

    // Download a file (authorized staff/patient only, checked via record access)
    @GetMapping("/api/files/{fileId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','PATIENT')")
    public ResponseEntity<InputStreamResource> download(
            @PathVariable String fileId,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) throws IOException {

        var res = gridFsService.download(fileId, principal, httpReq);

        String filename = res.getFilename() == null ? "download" : res.getFilename();
        String contentType = (res.getContentType() == null) ? MediaType.APPLICATION_OCTET_STREAM_VALUE : res.getContentType();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(res.getInputStream()));
    }
    
    @DeleteMapping("/api/records/{recordId}/files/{fileId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<com.mras.common.dto.ApiResponse<String>> delete(
            @PathVariable String recordId,
            @PathVariable String fileId,
            @AuthenticationPrincipal MrasUserPrincipal principal,
            HttpServletRequest httpReq
    ) {
        gridFsService.deleteFromRecord(recordId, fileId, principal, httpReq);
        return ResponseEntity.ok(com.mras.common.dto.ApiResponse.ok("File deleted", "OK"));
    }

}
