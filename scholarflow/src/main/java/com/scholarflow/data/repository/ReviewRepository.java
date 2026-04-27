package com.scholarflow.data.repository;

import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.Review;

public interface ReviewRepository {
    Optional<Review> findById(UUID id);

    // For every assignment is only one review
    Optional<Review> findByAssignmentId(UUID assignmentId);

    Review save(Review review);
    void delete(UUID id);
}
