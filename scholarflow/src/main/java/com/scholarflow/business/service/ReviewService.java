package com.scholarflow.business.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.Review;
import com.scholarflow.business.model.ReviewAssignment;
import com.scholarflow.business.model.User;
import com.scholarflow.business.model.enums.AssignmentStatus;
import com.scholarflow.business.model.enums.PaperStatus;
import com.scholarflow.business.model.enums.ReviewDecision;
import com.scholarflow.data.repository.PaperRepository;
import com.scholarflow.data.repository.ReviewAssignmentRepository;
import com.scholarflow.data.repository.ReviewRepository;

public final class ReviewService {
    private final ReviewAssignmentRepository assignments;
    private final ReviewRepository reviews;
    private final PaperRepository papers;

    public ReviewService(
        final ReviewAssignmentRepository assignments,
        final ReviewRepository reviews,
        final PaperRepository papers
    ) {
        this.assignments = Objects.requireNonNull(assignments, "Assignments repository is mandatory");
        this.reviews = Objects.requireNonNull(reviews, "Reviews repository is mandatory");
        this.papers = Objects.requireNonNull(papers, "Papers repository is mandatory");
    }

    // Assign reviewer on review
    public ReviewAssignment assign(
        final UUID paperId,
        final UUID reviewerId,
        final User admin,
        final LocalDate deadline
    ) {
        if (!admin.hasAdminPrivileges()) {
            throw new IllegalStateException("Access denied: Only administrators can assign reviewers.");
        }

        final Paper paper = this.papers.findById(paperId)
            .orElseThrow(() -> new IllegalArgumentException("Paper not found with ID: " + paperId));

        if (paper.submitterId().equals(reviewerId)) {
            throw new IllegalArgumentException("The author cannot be a reviewer of their own paper.");
        }

        if (paper.status() != PaperStatus.SUBMITTED && paper.status() != PaperStatus.UNDER_REVIEW) {
            throw new IllegalArgumentException(
                "Reviewers can only be assigned to papers with SUBMITTED or UNDER_REVIEW status."
            );
        }

        final ReviewAssignment newAssignment = new ReviewAssignment(
            paperId,
            reviewerId, 
            admin.id().orElseThrow(), 
            deadline
        );

        return this.assignments.save(newAssignment);
    }

    // Receiving and saving review from reviewer.
    public Review submitReview(
        final UUID assignmentId,
        final ReviewDecision decision,
        final String comments,
        final String privateNotes
    ) {
        final ReviewAssignment assignment = this.assignments.findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment ID is invalid."));

        if (!assignment.isActive()) {
            throw new IllegalStateException("Action prohibited.");
        }

        final Review review = new Review(assignmentId, decision, comments, privateNotes);

        // Save review to DB
        final Review savedReview = this.reviews.save(review);

        this.assignments.updateStatus(assignmentId, AssignmentStatus.COMPLETED);

        return savedReview;
    }

    public List<ReviewAssignment> reviewerTasks(final UUID reviewerId) {
        return this.assignments.findByReviewer(reviewerId);
    }

    public boolean areAllReviewsComplete(final UUID paperId) {
        final List<ReviewAssignment> paperAssignments = this.assignments.findByPaper(paperId);

        if (paperAssignments.isEmpty()) {
            return false;
        }

        return paperAssignments.stream()
            .allMatch(a -> a.status() == AssignmentStatus.COMPLETED);
    }
}
