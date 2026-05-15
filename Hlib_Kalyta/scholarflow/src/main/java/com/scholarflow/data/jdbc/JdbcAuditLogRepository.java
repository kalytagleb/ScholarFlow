package com.scholarflow.data.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scholarflow.business.model.AuditLogEntry;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.exception.RepositoryException;
import com.scholarflow.data.repository.AuditLogRepository;

public final class JdbcAuditLogRepository implements AuditLogRepository {
    private static final Logger log = LoggerFactory.getLogger(JdbcAuditLogRepository.class);

    private final DatabaseConnectionPool pool;

    public JdbcAuditLogRepository(final DatabaseConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public AuditLogEntry save(final AuditLogEntry entry) {
        final String sql = """
            INSERT INTO audit_log (user_id, action, entity_type, entity_id, details)
            VALUES (?, ?, ?, ?, ?)
            RETURNING *
            """;

        try (Connection conn = pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, entry.userId());
            stmt.setString(2, entry.action());
            stmt.setString(3, entry.entityType());
            stmt.setObject(4, entry.entityId());
            stmt.setString(5, entry.details());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuditLogEntry(
                        rs.getObject("id", UUID.class),
                        rs.getObject("user_id", UUID.class),
                        rs.getString("action"),
                        rs.getString("entity_type"),
                        rs.getObject("entity_id", UUID.class),
                        rs.getString("details"),
                        rs.getObject("created_at", java.time.LocalDateTime.class)
                    );
                }
                throw new RepositoryException("Audit log save returned no row");
            }
        } catch (SQLException ex) {
            log.error("Failed to save audit log entry: action={}", entry.action(), ex);
            throw new RepositoryException("Failed to save audit log entry", ex);
        }
    }
}
