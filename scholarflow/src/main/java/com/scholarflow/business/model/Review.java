package com.scholarflow.business.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.enums.ReviewDecision;

public final class Review {
    private final Optional<UUID> id;
    private final UUID assignmentId;
    private final ReviewDecision decision;
    private final String comments;
    private final Optional<String> privateNotes;
    private final Optional<LocalDateTime> submittedAt;

    // For loading from DB
    public Review(
        final UUID id,
        final UUID assignmentId,
        final String decision,
        final String comments,
        final String privateNotes,
        final LocalDateTime submittedAt
    ) {
        this.id = Optional.of(Objects.requireNonNull(id));
        this.assignmentId = Objects.requireNonNull(assignmentId);
        this.decision = ReviewDecision.valueOf(decision.toUpperCase());
        this.comments = Objects.requireNonNull(comments);
        this.privateNotes = Optional.ofNullable(privateNotes);
        this.submittedAt = Optional.of(Objects.requireNonNull(submittedAt));
    }

    // For creating new review.
    public Review(
        final UUID assignmentId,
        final ReviewDecision decision,
        final String comments,
        final String privateNotes
    ) {
        this.id = Optional.empty();
        this.assignmentId = Objects.requireNonNull(assignmentId);
        this.decision = Objects.requireNonNull(decision);
        this.comments = Objects.requireNonNull(comments);
        this.privateNotes = Optional.ofNullable(privateNotes);
        this.submittedAt = Optional.empty();

        // Comment cannot be too short
        if (comments.trim().length() < 20) {
            throw new IllegalArgumentException("Review comments must be at least 20 characters.");
        }
    }

    public Optional<UUID> id() { return id; }
    public UUID assignmentId() { return assignmentId; }
    public ReviewDecision decision() { return decision; }
    public String comments() { return comments; }
    public Optional<String> privateNotes() { return privateNotes; }
    public Optional<LocalDateTime> submittedAt() { return submittedAt; }

    // Behavior
    public boolean isPositive() {
        return this.decision == ReviewDecision.ACCEPT ||
               this.decision == ReviewDecision.MINOR_REVISION;
    }

    @Override
    public String toString() {
        return String.format("Review{assignment=%s, decision=%s}", assignmentId, decision);
    }
}
