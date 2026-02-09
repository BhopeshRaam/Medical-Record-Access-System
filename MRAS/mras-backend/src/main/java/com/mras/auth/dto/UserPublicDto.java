package com.mras.auth.dto;

import java.util.Set;

import com.mras.common.enums.Role;

public class UserPublicDto {
    private String id;
    private String name;
    private String email;
    private Set<Role> roles;

    public UserPublicDto(String id, String name, String email, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Set<Role> getRoles() { return roles; }
}
