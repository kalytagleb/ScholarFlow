package com.scholarflow.business.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.User;
import com.scholarflow.data.repository.UserRepository;

public final class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
            .filter(user -> user.passwordMatches(password));
    }

    public User registerUser(String username, String plainPassword, String email, String fullName, String role, UUID fieldId) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username '" + username + "' already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email '" + email + "' already exists");
        }

        final User newUser = new User(username, plainPassword, email, fullName, role, fieldId);
        return userRepository.save(newUser);
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAllActive() {
        return userRepository.findAllActive();
    }

    public List<User> findByRole(final String role) {
        if (role == null || role.isBlank()) {
            return Collections.emptyList();
        }

        return userRepository.findByRole(role.toUpperCase());
    }

    public List<User> findAllReviewers() {
        return this.findByRole("REVIEWER");
    }

    public void changeRole(final UUID userId, final String newRole, final User admin) {
        Objects.requireNonNull(userId, "userId required");
        Objects.requireNonNull(newRole, "newRole required");
        if (!admin.hasAdminPrivileges()) {
            throw new IllegalStateException("Only administrators can change roles");
        }
        final User existing = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        final User updated = new User(
            userId,
            existing.username(),
            existing.passwordHash(),
            existing.email(),
            existing.fullName(),
            newRole,
            existing.fieldId().orElse(null),
            existing.isActive(),
            existing.createdAt().orElseThrow()
        );
        userRepository.update(updated);
    }

    public void deactivateUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        userRepository.deactivate(id);
    }

    public User updateProfile(UUID id, String fullName, String email) {

        User existing = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (!existing.email().equalsIgnoreCase(email) && userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email '" + email + "' is already taken");
        }

        User updated = new User(
            id,
            existing.username(),
            existing.passwordHash(),
            email,
            fullName,
            existing.role(),
            existing.fieldId().orElse(null),
            existing.isActive(),
            existing.createdAt().orElseThrow()
        );

        userRepository.update(updated);
        return updated;
    }
}
