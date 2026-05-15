package com.scholarflow.business.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.enums.AssignmentStatus;

public final class ReviewAssignment {
    private final Optional<UUID> id;
    private final UUID paperId;
    private final UUID reviewerId;
    private final UUID assignedBy;
    private final Optional<LocalDateTime> assignedAt;
    private final LocalDate deadline;
    private final AssignmentStatus status;

    // For loading from DB
    public ReviewAssignment(
        final UUID id,
        final UUID paperId,
        final UUID reviewerId,
        final UUID assignedBy,
        final LocalDateTime assignedAt,
        final LocalDate deadline,
        final String status
    ) {
        this.id = Optional.of(Objects.requireNonNull(id));
        this.paperId = Objects.requireNonNull(paperId);
        this.reviewerId = Objects.requireNonNull(reviewerId);
        this.assignedBy = Objects.requireNonNull(assignedBy);
        this.assignedAt = Optional.of(Objects.requireNonNull(assignedAt));
        this.deadline = Objects.requireNonNull(deadline);
        this.status = AssignmentStatus.valueOf(status.toUpperCase());
    }

    // For creating new assignment
    public ReviewAssignment(
        final UUID paperId,
        final UUID reviewerId,
        final UUID assignedBy,
        final LocalDate deadline
    ) {
        this.id = Optional.empty();
        this.paperId = Objects.requireNonNull(paperId);
        this.reviewerId = Objects.requireNonNull(reviewerId);
        this.assignedBy = Objects.requireNonNull(assignedBy);
        this.deadline = Objects.requireNonNull(deadline);
        this.status = AssignmentStatus.PENDING;
        this.assignedAt = Optional.empty();
        
        // Deadline validation. We can create deadline only in the future time
        if (deadline.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }
    }

    public Optional<UUID> id() { return id; }
    public UUID paperId() { return paperId; }
    public UUID reviewerId() { return reviewerId; }
    public UUID assignedBy() { return assignedBy; }
    public Optional<LocalDateTime> assignedAt() { return assignedAt; }
    public LocalDate deadline() { return deadline; }
    public AssignmentStatus status() { return status; }

    // Behavior
    public boolean isOverdue() {
        return this.status == AssignmentStatus.PENDING &&
               this.deadline.isBefore(LocalDate.now());
    }

    public boolean isActive() {
        return this.status == AssignmentStatus.PENDING;
    }

    // Create copy of assignment with changed status
    public ReviewAssignment withStatus(final AssignmentStatus next) {
        return new ReviewAssignment(
            this.id.orElse(null),
            this.paperId, 
            this.reviewerId, 
            this.assignedBy,
            this.assignedAt.orElse(null),
            this.deadline,
            next.name()
        );
    }

    @Override
    public String toString() {
        return String.format("Assignment{paper=%s, reviewer=%s, status=%s, deadline=%s}",
            paperId, reviewerId, status, deadline);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ReviewAssignment other)) return false;
        return id.isPresent() && other.id.isPresent() && id.get().equals(other.id.get());
    }

    @Override
    public int hashCode() {
        return id.map(UUID::hashCode).orElse(0);
    }
}
