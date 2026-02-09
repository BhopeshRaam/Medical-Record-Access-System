package com.mras.records.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mras.common.enums.RecordStatus;
import com.mras.records.model.Record;

import java.util.List;

public interface RecordRepository extends MongoRepository<Record, String> {
    Page<Record> findByPatientId(String patientId, Pageable pageable);
	Page<Record> findByPatientIdAndStatusNot(String patientId, RecordStatus status, Pageable pageable);
	Page<Record> findByPatientIdAndStatusNotIn(String patientId, List<RecordStatus> statuses, Pageable pageable);
}
