package com.scholarflow.business.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.enums.PaperStatus;

public final class StatusChange {
    private final Optional<UUID> id;
    private final UUID paperId;
    private final Optional<PaperStatus> oldStatus;
    private final PaperStatus newStatus;
    private final UUID changedBy;
    private final Optional<String> comment;
    private final LocalDateTime changedAt;

    // For loading from DB
    public StatusChange(
        final UUID id, 
        final UUID paperId, 
        final PaperStatus oldStatus, 
        final PaperStatus newStatus, 
        final UUID changedBy, 
        final String comment, 
        final LocalDateTime changedAt
    ) {
        this.id = Optional.of(Objects.requireNonNull(id));
        this.paperId = Objects.requireNonNull(paperId);
        this.oldStatus = Optional.ofNullable(oldStatus);
        this.newStatus = Objects.requireNonNull(newStatus);
        this.changedBy = Objects.requireNonNull(changedBy);
        this.comment = Optional.ofNullable(comment);
        this.changedAt = Objects.requireNonNull(changedAt);
    }

    // For creating new record 
    public StatusChange(
        final UUID paperId, 
        final PaperStatus oldStatus, 
        final PaperStatus newStatus, 
        final UUID changedBy, 
        final String comment
    ) {
        this.id = Optional.empty();
        this.paperId = Objects.requireNonNull(paperId);
        this.oldStatus = Optional.ofNullable(oldStatus);
        this.newStatus = Objects.requireNonNull(newStatus);
        this.changedBy = Objects.requireNonNull(changedBy);
        this.comment = Optional.ofNullable(comment);
        this.changedAt = LocalDateTime.now();
    }

    public Optional<UUID> id() {return id;}
    public UUID paperId() { return paperId; }
    public Optional<PaperStatus> oldStatus() { return oldStatus; }
    public PaperStatus newStatus() { return newStatus; }
    public UUID changedBy() { return changedBy; }
    public Optional<String> comment() { return comment; }
    public LocalDateTime changedAt() { return changedAt; }

    // Behavior
    public boolean isInitialSubmission() {
        return oldStatus.isEmpty() && newStatus == PaperStatus.DRAFT;
    }

    public boolean isFinalDecision() {
        return newStatus == PaperStatus.ACCEPTED || newStatus == PaperStatus.REJECTED;
    }

    @Override
    public String toString() {
        return String.format("StatusChange{paper=%s, transition=%s -> %s, by=%s}",
            paperId, 
            oldStatus.map(Enum::name).orElse("NONE"), 
            newStatus, 
            changedBy
        );
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof StatusChange other)) return false;
        return id.isPresent() && other.id.isPresent() && id.get().equals(other.id.get());
    }

    @Override
    public int hashCode() {
        return id.map(UUID::hashCode).orElse(0);
    }
}
