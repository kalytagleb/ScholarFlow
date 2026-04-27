package com.scholarflow.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.ReviewAssignment;
import com.scholarflow.business.model.enums.AssignmentStatus;

public interface ReviewAssignmentRepository {
    Optional<ReviewAssignment> findById(UUID id);
    List<ReviewAssignment> findByPaper(UUID paperId);
    List<ReviewAssignment> findByReviewer(UUID reviewerId);
    ReviewAssignment save(ReviewAssignment assignment);
    void updateStatus(UUID id, AssignmentStatus status);
    void delete(UUID id);
}
