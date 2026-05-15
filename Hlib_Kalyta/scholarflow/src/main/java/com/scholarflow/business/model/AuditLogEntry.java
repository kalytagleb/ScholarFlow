package com.scholarflow.business.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class AuditLogEntry {
    private final Optional<UUID> id;
    private final UUID userId;
    private final String action;
    private final String entityType;
    private final UUID entityId;
    private final String details;
    private final Optional<LocalDateTime> createdAt;

    public AuditLogEntry(
        final UUID userId,
        final String action,
        final String entityType,
        final UUID entityId,
        final String details
    ) {
        this.id = Optional.empty();
        this.userId = userId;
        this.action = Objects.requireNonNull(action, "action required");
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.createdAt = Optional.empty();
    }

    public AuditLogEntry(
        final UUID id,
        final UUID userId,
        final String action,
        final String entityType,
        final UUID entityId,
        final String details,
        final LocalDateTime createdAt
    ) {
        this.id = Optional.ofNullable(id);
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.createdAt = Optional.ofNullable(createdAt);
    }

    public Optional<UUID> id() { return id; }
    public UUID userId() { return userId; }
    public String action() { return action; }
    public String entityType() { return entityType; }
    public UUID entityId() { return entityId; }
    public String details() { return details; }
    public Optional<LocalDateTime> createdAt() { return createdAt; }
}
