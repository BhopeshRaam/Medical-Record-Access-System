package com.mras.admin.users.dto;

import java.time.Instant;
import java.util.Set;

import com.mras.common.enums.Role;
import com.mras.common.enums.UserStatus;
import com.mras.users.model.User;

public class AdminUserResponse {
    private String id;
    private String name;
    private String email;
    private Set<Role> roles;
    private UserStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public static AdminUserResponse from(User u) {
        AdminUserResponse r = new AdminUserResponse();
        r.id = u.getId();
        r.name = u.getName();
        r.email = u.getEmail();
        r.roles = u.getRoles();
        r.status = u.getStatus();
        r.createdAt = u.getCreatedAt();
        r.updatedAt = u.getUpdatedAt();
        return r;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Set<Role> getRoles() { return roles; }
    public UserStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
