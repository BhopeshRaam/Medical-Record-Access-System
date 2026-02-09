package com.mras.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mras.auth.jwt.MrasUserPrincipal;
import com.mras.common.enums.UserStatus;
import com.mras.users.model.User;
import com.mras.users.repo.UserRepository;

@Service
public class MrasUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public MrasUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean enabled = u.getStatus() == UserStatus.ACTIVE;

        return new MrasUserPrincipal(
                u.getId(),
                u.getEmail(),
                u.getPasswordHash(),
                enabled,
                u.getRoles()
        );
    }
}
