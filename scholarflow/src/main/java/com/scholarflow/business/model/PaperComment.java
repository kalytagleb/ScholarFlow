package com.scholarflow.business.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PaperComment {
    private final Optional<UUID> id;
    private final UUID paperId;
    private final UUID userId;
    private final String authorName;
    private final String content;
    private final Optional<LocalDateTime> createdAt;

    // For loading from DB
    public PaperComment(
        final UUID id,
        final UUID paperId,
        final UUID userId,
        final String authorName,
        final String content,
        final LocalDateTime createdAt
    ) {
        this.id = Optional.of(Objects.requireNonNull(id, "ID is required for existing comment"));
        this.paperId = Objects.requireNonNull(paperId, "Paper ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.authorName = Objects.requireNonNull(authorName);
        this.content = this.validateContent(content);
        this.createdAt = Optional.of(Objects.requireNonNull(createdAt, "Timestamp is required"));
    }

    // For creating new comment by reader.
    public PaperComment(final UUID paperId, final UUID userId, final String authorName, final String content) {
        this.id = Optional.empty();
        this.paperId = Objects.requireNonNull(paperId);
        this.userId = Objects.requireNonNull(userId);
        this.authorName = Objects.requireNonNull(authorName);
        this.content = this.validateContent(content);
        this.createdAt = Optional.empty();
    }

    private String validateContent(final String text) {
        final String trimmed = Objects.requireNonNull(text, "Comment cannot be null").trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty.");
        }

        if (trimmed.length() > 2000) {
            throw new IllegalArgumentException("Comment is too long (max 2000 characters).");
        }
        return trimmed;
    }

    public Optional<UUID> id() {
        return id;
    }

    public UUID paperId() {
        return paperId;
    }

    public UUID userId() {
        return userId;
    }

    public String authorName() {
        return authorName;
    }

    public String content() {
        return content;
    }

    public Optional<LocalDateTime> createdAt() {
        return createdAt;
    }

    public boolean isOwnedBy(final UUID authorId) {
        return this.userId.equals(authorId);
    }

    @Override
    public String toString() {
        return String.format("Comment{user=%s, paper=%s, content='%s...'}", 
            userId, paperId, content.substring(0, Math.min(content.length(), 20)));
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PaperComment other)) return false;
        return id.isPresent() && other.id.isPresent() && id.get().equals(other.id.get());
    }

    @Override
    public int hashCode() {
        return id.map(UUID::hashCode).orElse(0);
    }
}
