package com.mras.users.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mras.users.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("{ $or: [ { name: { $regex: ?0, $options: 'i' } }, { email: { $regex: ?0, $options: 'i' } } ] }")
    Page<User> search(String q, Pageable pageable);
}
