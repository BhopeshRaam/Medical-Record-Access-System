package com.mras.auth.service;

import org.springframework.stereotype.Service;

import com.mras.auth.dto.MeResponse;
import com.mras.common.exception.NotFoundException;
import com.mras.users.repo.UserRepository;

@Service
public class MeService {

    private final UserRepository userRepo;

    public MeService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public MeResponse getMeByEmail(String email) {
        var u = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return new MeResponse(u.getId(), u.getName(), u.getEmail(), u.getRoles());
    }
}
