package com.mras.users.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mras.common.enums.Role;
import com.mras.common.enums.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank
    @Size(min = 2, max = 120)
    private String name;

    @NotBlank
    @Email
    @Size(max = 200)
    @Indexed(unique = true)
    private String email;

    /**
     * Store a bcrypt hash here (never store raw password).
     */
    @NotBlank
    @Size(min = 20, max = 200)
    private String passwordHash;

    @NotNull
    private Set<Role> roles = new HashSet<>();

    @NotNull
    private UserStatus status = UserStatus.ACTIVE;

    @NotNull
    private Instant createdAt = Instant.now();

    @NotNull
    private Instant updatedAt = Instant.now();

    public User() {}

    // --- convenience ---
    public void touchUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    // --- getters/setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
