package com.scholarflow.business.model;

import java.util.Objects;
import java.util.UUID;

public final class PaperAuthor {
    private final UUID paperId;
    private final UUID userId;
    private final int order;

    public PaperAuthor(final UUID paperId, final UUID userId, final int order) {
        this.paperId = Objects.requireNonNull(paperId, "Paper ID is required");
        this.userId = Objects.requireNonNull(userId, "User ID is required");
        this.order = this.validateOrder(order);
    }

    private int validateOrder(final int order) {
        if (order <= 0) {
            throw new IllegalArgumentException("Author order must be positive.");
        }

        return order;
    }

    public UUID paperId() {
        return paperId;
    }

    public UUID userId() {
        return userId;
    }

    public int order() {
        return order;
    }

    public boolean isFirstAuthor() {
        return this.order == 1;
    }

    @Override
    public String toString() {
        return String.format("PaperAuthor{paper=%s, user=%s, order=%d}", paperId, userId, order);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PaperAuthor other)) return false;
        return Objects.equals(paperId, other.paperId) && 
               Objects.equals(userId, other.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paperId, userId);
    }
}
