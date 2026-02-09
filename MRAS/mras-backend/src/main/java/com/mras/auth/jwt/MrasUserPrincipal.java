package com.mras.auth.jwt;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mras.common.enums.Role;

public class MrasUserPrincipal implements UserDetails {

	//--//
	private static final long serialVersionUID = 1L;
	private final String id;
    private final String email;
    private final String passwordHash;
    private final boolean enabled;
    private final Set<Role> roles;

    public MrasUserPrincipal(String id, String email, String passwordHash, boolean enabled, Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security expects ROLE_ prefix for hasRole checks
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}
