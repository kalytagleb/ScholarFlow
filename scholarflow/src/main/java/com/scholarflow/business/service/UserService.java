package com.scholarflow.business.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.User;
import com.scholarflow.data.repository.UserRepository;

public final class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register new user
    public User registerUser(String username, String passwordHash, String email, String fullName, String role, UUID fieldId) {
        
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
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
        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(() -> 
            new IllegalArgumentException("User not found with id: " + id)
        );

        User updatedUser = new User(
            user.id(),
            user.username(),
            user.passwordHash(),
            email,
            fullName,
            user.role(),
            user.fieldId(),
            user.isActive(),
            user.createdAt()
        );

        userRepository.update(updatedUser);
        return updatedUser;
    }
}
