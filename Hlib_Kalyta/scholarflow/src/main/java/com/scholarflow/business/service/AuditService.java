package com.scholarflow.business.service;

import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scholarflow.business.model.AuditLogEntry;
import com.scholarflow.data.repository.AuditLogRepository;

public final class AuditService {
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository repository;

    public AuditService(final AuditLogRepository repository) {
        this.repository = repository;
    }

    public void log(
        final UUID actorId,
        final String action,
        final String entityType,
        final UUID entityId,
        final String details
    ) {
        Objects.requireNonNull(action, "action required");
        try {
            repository.save(new AuditLogEntry(actorId, action, entityType, entityId, details));
            log.info("AUDIT actor={} action={} entity={}:{}", actorId, action, entityType, entityId);
        } catch (Exception e) {
            log.error("Failed to persist audit event: action={}", action, e);
        }
    }
}
