package com.scholarflow.business.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import com.scholarflow.business.model.User;
import com.scholarflow.data.repository.UserRepository;

public final class UserService {

    private final UserRepository userRepository;

    private final Pattern emailPattern = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
        Pattern.CASE_INSENSITIVE
    );

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (!emailPattern.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
    }

    // Register new user
    public User registerUser(String username, String passwordHash, String email, String fullName, String role, UUID fieldId) {
        
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        this.validateEmail(email);

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username '" + username + "' already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email '" + email + "' already exists");
        }

        User newUser = new User(username, passwordHash, email, fullName, role, fieldId);
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

    public void deactivateUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        userRepository.deactivate(id);
    }

    public User updateProfile(UUID id, String fullName, String email) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }

        this.validateEmail(email);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (!user.email().equalsIgnoreCase(email)) {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Email '" + email + "' is already taken");
            }
        }

        User updatedUser = new User(
            user.id().orElseThrow(),
            user.username(),
            user.passwordHash(),
            email,
            fullName,
            user.role(),
            user.fieldId().orElse(null),
            user.isActive(),
            user.createdAt().orElseThrow()
        );

        userRepository.update(updatedUser);
        return updatedUser;
    }
}
