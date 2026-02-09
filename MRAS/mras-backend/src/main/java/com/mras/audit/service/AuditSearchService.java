package com.mras.audit.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mras.audit.model.AuditLog;
import com.mras.common.enums.AuditAction;
import com.mras.common.enums.ResourceType;

@Service
public class AuditSearchService {

    private final MongoTemplate mongoTemplate;

    public AuditSearchService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Page<AuditLog> search(
            String actorId,
            AuditAction action,
            ResourceType resourceType,
            String resourceId,
            String status,
            Instant from,
            Instant to,
            Pageable pageable
    ) {
        Query query = new Query();

        List<Criteria> criteriaList = new ArrayList<>();

        if (actorId != null && !actorId.isBlank()) {
            criteriaList.add(Criteria.where("actorId").is(actorId.trim()));
        }
        if (action != null) {
            criteriaList.add(Criteria.where("action").is(action));
        }
        if (resourceType != null) {
            criteriaList.add(Criteria.where("resourceType").is(resourceType));
        }
        if (resourceId != null && !resourceId.isBlank()) {
            criteriaList.add(Criteria.where("resourceId").is(resourceId.trim()));
        }
        if (status != null && !status.isBlank()) {
            criteriaList.add(Criteria.where("status").is(status.trim().toUpperCase()));
        }
        if (from != null || to != null) {
            Criteria time = Criteria.where("timestamp");
            if (from != null) time = time.gte(from);
            if (to != null) time = time.lte(to);
            criteriaList.add(time);
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList));
        }

        // Count first (without paging)
        long total = mongoTemplate.count(query, AuditLog.class);

        // Apply paging & sorting
        query.with(pageable);

        List<AuditLog> items = mongoTemplate.find(query, AuditLog.class);

        return new PageImpl<>(items, pageable, total);
    }
}
