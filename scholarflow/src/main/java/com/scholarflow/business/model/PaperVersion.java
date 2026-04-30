package com.scholarflow.business.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PaperVersion {
    private final Optional<UUID> id;
    private final UUID paperId;
    private final int number;
    private final String title;
    private final String paperAbstract;
    private final Optional<String> content;
    private final Optional<LocalDateTime> createdAt;

    // For loading from DB
    public PaperVersion(
        final UUID id,
        final UUID paperId,
        final int number,
        final String title,
        final String paperAbstract,
        final String content,
        final LocalDateTime createdAt
    ) {
        this.id = Optional.of(Objects.requireNonNull(id));
        this.paperId = Objects.requireNonNull(paperId);
        this.number = this.validateNumber(number);
        this.title = Objects.requireNonNull(title);
        this.paperAbstract = Objects.requireNonNull(paperAbstract);
        this.content = Optional.ofNullable(content);
        this.createdAt = Optional.of(Objects.requireNonNull(createdAt));
    }

    // For creating new version
     public PaperVersion(
        final UUID paperId,
        final int number,
        final String title,
        final String paperAbstract,
        final String content
    ) {
        this.id = Optional.empty();
        this.paperId = Objects.requireNonNull(paperId);
        this.number = this.validateNumber(number);
        this.title = Objects.requireNonNull(title);
        this.paperAbstract = Objects.requireNonNull(paperAbstract);
        this.content = Optional.ofNullable(content);
        this.createdAt = Optional.empty();
    }

    private int validateNumber(final int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("Version must be positive");
        }
        return num;
    }

    public Optional<UUID> id() { return id; }
    public UUID paperId() { return paperId; }
    public int number() { return number; }
    public String title() { return title; }
    public String paperAbstract() { return paperAbstract; }
    public Optional<String> content() { return content; }
    public Optional<LocalDateTime> createdAt() { return createdAt; }

    public boolean isFirst() {
        return this.number == 1;
    }

    public int wordCount() {
        return this.content
            .map(text -> {
                final String trimmed = text.trim();
                return trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
            })
            .orElse(0);
    }

    @Override
    public String toString() {
        return String.format("PaperVersion{paper=%s, v%d, title='%s'}", 
            paperId, number, title);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PaperVersion other)) return false;
        if (id.isPresent() && other.id.isPresent()) {
            return id.get().equals(other.id.get());
        }
        return number == other.number && paperId.equals(other.paperId);
    }

    @Override
    public int hashCode() {
        return id.isPresent() ? id.get().hashCode() : Objects.hash(paperId, number);
    }
}