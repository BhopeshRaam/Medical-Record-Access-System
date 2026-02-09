package com.mras.auth.dto;

import java.util.Set;

import com.mras.common.enums.Role;

public class LoginResponse {
    private String token;
    private String userId;
    private String email;
    private Set<Role> roles;

    public LoginResponse(String token, String userId, String email, Set<Role> roles) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public Set<Role> getRoles() { return roles; }
}
