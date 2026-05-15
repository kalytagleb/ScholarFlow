package com.scholarflow.data.repository;

import com.scholarflow.business.model.AuditLogEntry;

public interface AuditLogRepository {
    AuditLogEntry save(AuditLogEntry entry);
}
