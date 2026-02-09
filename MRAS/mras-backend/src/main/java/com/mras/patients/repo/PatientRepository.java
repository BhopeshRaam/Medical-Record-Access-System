package com.mras.patients.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mras.common.enums.PatientStatus;
import com.mras.patients.model.Patient;

public interface PatientRepository extends MongoRepository<Patient, String> {

    Optional<Patient> findByMrn(String mrn);
    Optional<Patient> findByLinkedUserId(String linkedUserId);
    Optional<Patient> findByLinkedUserIdAndStatusNot(String linkedUserId, PatientStatus status);
    boolean existsByMrn(String mrn);
    boolean existsByLinkedUserId(String linkedUserId);

    Page<Patient> findByStatusNot(PatientStatus status, Pageable pageable);

    // Paged search by name/mrn/phone
    @Query("{ $and: [ { status: { $ne: 'ARCHIVED' } }, { $or: [ " +
           "{ name: { $regex: ?0, $options: 'i' } }, " +
           "{ mrn:  { $regex: ?0, $options: 'i' } }, " +
           "{ phone: { $regex: ?0 } } " +
           "] } ] }")
    Page<Patient> search(String q, Pageable pageable);


// Paged search by name/mrn/phone (includes archived)
@Query("{ $or: [ " +
       "{ name: { $regex: ?0, $options: 'i' } }, " +
       "{ mrn:  { $regex: ?0, $options: 'i' } }, " +
       "{ phone: { $regex: ?0 } } " +
       "] }")
Page<Patient> searchAll(String q, Pageable pageable);

}
