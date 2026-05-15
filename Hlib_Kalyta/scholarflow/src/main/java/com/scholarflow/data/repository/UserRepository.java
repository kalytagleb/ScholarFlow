package com.scholarflow.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.scholarflow.business.model.User;

public interface UserRepository {
    Optional<User> findById(UUID id);
    
    /**
     * Find User by username.
     * Used when user want to log in
     */
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    /**
     * Returns all active users
     */
    List<User> findAllActive();

    /**
     * Returns all users with specific roles
     */
    List<User> findByRole(String role);

    /**
     * Save new user in DB
     */
    User save(User user);

    /**
     * Update current user
     */
    void update(User user);

    void deactivate(UUID id);
}
