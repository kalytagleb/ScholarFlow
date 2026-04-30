package com.scholarflow.business.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PaperLike {
    private final UUID paperId;
    private final UUID userId;
    private final Optional<LocalDateTime> createdAt;

    // For loading from DB
    public PaperLike(final UUID paperId, final UUID userId, final LocalDateTime createdAt) {
        this.paperId = Objects.requireNonNull(paperId);
        this.userId = Objects.requireNonNull(userId);
        this.createdAt = Optional.of(Objects.requireNonNull(createdAt));
    }

    // For creating new like.
    public PaperLike(final UUID paperId, final UUID userId) {
        this.paperId = Objects.requireNonNull(paperId);
        this.userId = Objects.requireNonNull(userId);
        this.createdAt = Optional.empty();
    }

    public UUID paperId() {
        return paperId;
    }

    public UUID userId() {
        return userId;
    }

    public Optional<LocalDateTime> createdAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("PaperLike{paper=%s, user=%s}", paperId, userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaperLike other)) return false;
        return paperId.equals(other.paperId) && userId.equals(other.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paperId, userId);
    }
}
