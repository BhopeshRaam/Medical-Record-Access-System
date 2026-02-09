package com.mras.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mras.auth.dto.LoginRequest;
import com.mras.auth.dto.LoginResponse;
import com.mras.auth.dto.RegisterRequest;
import com.mras.auth.jwt.JwtService;
import com.mras.common.enums.UserStatus;
import com.mras.users.model.User;
import com.mras.users.repo.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail().toLowerCase().trim());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setRoles(req.getRoles());
        u.setStatus(UserStatus.ACTIVE);

        return userRepo.save(u);
    }

    public LoginResponse login(LoginRequest req) {
        User u = userRepo.findByEmail(req.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (u.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("User is disabled");
        }

        String token = jwtService.generateToken(u.getId(), u.getEmail(), u.getRoles());
        return new LoginResponse(token, u.getId(), u.getEmail(), u.getRoles());
    }
    
    public com.mras.users.model.User getUserByEmailForAudit(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for audit"));
    }

}
