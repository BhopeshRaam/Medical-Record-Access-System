package com.mras.admin.users.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mras.admin.users.dto.AdminUserUpdateRequest;
import com.mras.common.exception.NotFoundException;
import com.mras.users.model.User;
import com.mras.users.repo.UserRepository;

@Service
public class AdminUserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> search(String q, Pageable pageable) {
        if (q == null || q.trim().isEmpty()) {
            return userRepo.findAll(pageable);
        }
        return userRepo.search(q.trim(), pageable);
    }

    public User getById(String id) {
        return userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User update(String id, AdminUserUpdateRequest req) {
        User u = getById(id);
        if (req.getName() != null) u.setName(req.getName().trim());
        if (req.getRoles() != null) u.setRoles(req.getRoles());
        if (req.getStatus() != null) u.setStatus(req.getStatus());
        u.touchUpdatedAt();
        return userRepo.save(u);
    }

    public User resetPassword(String id, String newPassword) {
        User u = getById(id);
        u.setPasswordHash(passwordEncoder.encode(newPassword));
        u.touchUpdatedAt();
        return userRepo.save(u);
    }
}
