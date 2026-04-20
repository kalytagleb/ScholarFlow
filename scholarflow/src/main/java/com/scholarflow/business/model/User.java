package com.scholarflow.business.model;

import java.util.Objects;
import java.util.UUID;
import java.time.LocalDateTime;

public final class User {
    private final UUID id;
    private final String username;
    private final String passwordHash;
    private final String email;
    private final String fullName;
    private final String role;
    private final UUID fieldId;
    private final boolean active;
    private final LocalDateTime createdAt;

    // Full constructor (for reading from DB)
    public User(
        final UUID id,
        final String username,
        final String passwordHash,
        final String email,
        final String fullName,
        final String role,
        final UUID fieldId,
        final boolean active,
        final LocalDateTime createdAt
    ) {
        this.id = id;
        this.username = Objects.requireNonNull(username, "username cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "fullName cannot be null");
        this.role = Objects.requireNonNull(role, "usename cannot be null");
        this.fieldId = fieldId;
        this.active = active;
        this.createdAt = createdAt;
    }

    // For creating new user
    public User (
        final String username,
        final String passwordHash,
        final String email,
        final String fullName,
        final String role,
        final UUID fieldId
    ) {
        this(
            null,
            username,
            passwordHash,
            email,
            fullName,
            role,
            fieldId,
            true,
            null
        );
    }

    public UUID id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public String email() {
        return email;
    }

    public String fullName() {
        return fullName;
    }

    public String role() {
        return role;
    }

    public UUID fieldId() {
        return fieldId;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    // Object must have behavior (not only data storage)
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    public boolean isResearcher() {
        return "RESEARCHER".equals(this.role);
    }

    public boolean isReviewer() {
        return "REVIEWER".equals(this.role);
    }

    @Override
    public String toString() {
        return String.format("User{id=%s, username='%s', role='%s', active=%b}",
            id, username, role, active
         );
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}