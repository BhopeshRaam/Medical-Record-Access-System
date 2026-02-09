package com.mras.audit.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mras.audit.model.AuditLog;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}
