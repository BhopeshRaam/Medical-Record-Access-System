package com.mras.admin.users.dto;

import java.util.Set;

import com.mras.common.enums.Role;
import com.mras.common.enums.UserStatus;

public class AdminUserUpdateRequest {
    private String name;
    private Set<Role> roles;
    private UserStatus status; // ACTIVE / DISABLED / LOCKED

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
}
