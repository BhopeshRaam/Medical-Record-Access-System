package com.mras.access.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mras.access.model.AccessGrant;
import com.mras.common.enums.AccessGrantStatus;

public interface AccessGrantRepository extends MongoRepository<AccessGrant, String> {

    Optional<AccessGrant> findByPatientIdAndGranteeUserIdAndStatus(String patientId, String granteeUserId, AccessGrantStatus status);

    List<AccessGrant> findByPatientIdAndStatus(String patientId, AccessGrantStatus status);

    List<AccessGrant> findByGranteeUserIdAndStatus(String granteeUserId, AccessGrantStatus status);
}
