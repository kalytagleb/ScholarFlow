package com.scholarflow.business.model;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;

public final class User {
    private static final Pattern EMAIL_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private final Optional<UUID> id;
    private final String username;
    private final String passwordHash;
    private final String email;
    private final String fullName;
    private final String role;
    private final Optional<UUID> fieldId;
    private final boolean active;
    private final Optional<LocalDateTime> createdAt;

    // Full constructor (for reading from DB)
    public User(
        UUID id,
        String username,
        String passwordHash,
        String email,
        String fullName,
        String role,
        UUID fieldId,
        boolean active,
        LocalDateTime createdAt
    ) {
        this.id = Optional.of(Objects.requireNonNull(id, "ID cannot be null in this constructor"));
        this.username = Objects.requireNonNull(username, "username cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash cannot be null");
        this.email = validateEmail(email);
        this.fullName = Objects.requireNonNull(fullName, "fullName cannot be null");
        this.role = Objects.requireNonNull(role, "role cannot be null");
        this.fieldId = Optional.ofNullable(fieldId);
        this.active = active;
        this.createdAt = Optional.of(Objects.requireNonNull(createdAt, "createdAt cannot be null"));
    }

    // For creating new user
    public User (
        String username,
        String plainPassword,
        String email,
        String fullName,
        String role,
        UUID fieldId
    ) {
        this.id = Optional.empty();
        this.username = Objects.requireNonNull(username);
        this.passwordHash = hash(plainPassword);
        this.email = validateEmail(email);
        this.fullName = Objects.requireNonNull(fullName);
        this.role = Objects.requireNonNull(role);
        this.fieldId = Optional.ofNullable(fieldId);
        this.active = true;
        this.createdAt = Optional.empty();
    }

    private String hash(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    // Object checks his password
    public boolean passwordMatches(String candidate) {
        return BCrypt.checkpw(candidate, this.passwordHash);
    }

    private String validateEmail(String email) {
        if (email == null || !EMAIL_REGEX.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        return email;
    }

    public Optional<UUID> id() {
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

    public Optional<UUID> fieldId() {
        return fieldId;
    }

    public boolean isActive() {
        return active;
    }

    public Optional<LocalDateTime> createdAt() {
        return createdAt;
    }

    // Object must have behavior (not only data storage)
    public boolean hasAdminPrivileges() {
        return "ADMIN".equals(this.role);
    }

    public boolean canSubmitPapers() {
        return "RESEARCHER".equals(this.role) || "ADMIN".equals(this.role);
    }

    public boolean canReview() {
        return "REVIEWER".equals(this.role) || "ADMIN".equals(this.role);
    }

    public boolean canRead() {
        return "READER".equals(this.role) || "ADMIN".equals(this.role);
    }

    public boolean canInteract() {
        return this.active;
    }

    @Override
    public String toString() {
        return String.format("User{id=%s, username='%s', role='%s'}",
            id.map(UUID::toString).orElse("NEW"),
            username, 
            role
        );
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User other)) return false;
        return id.isPresent() && other.id.isPresent() && id.get().equals(other.id.get());
    }

    @Override
    public int hashCode() {
        return id.map(UUID::hashCode).orElse(0);
    }
}