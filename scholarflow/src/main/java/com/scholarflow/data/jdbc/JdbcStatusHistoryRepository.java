package com.scholarflow.data.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.scholarflow.business.model.StatusChange;
import com.scholarflow.business.model.enums.PaperStatus;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.exception.RepositoryException;
import com.scholarflow.data.repository.StatusHistoryRepository;

public final class JdbcStatusHistoryRepository implements StatusHistoryRepository {
    private final DatabaseConnectionPool pool;

    public JdbcStatusHistoryRepository(final DatabaseConnectionPool pool) {
        this.pool = Objects.requireNonNull(pool, "DB pool cannot be null");
    }

    @Override
    public void save(final StatusChange change) {
        final String sql = """
                INSERT INTO status_history (paper_id, old_status, new_status, changed_by, comment, changed_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = this.pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, change.paperId());
            stmt.setString(2, change.oldStatus()
                .map(Enum::name)
                .orElse(null));
            stmt.setString(3, change.newStatus().name());
            stmt.setObject(4, change.changedBy());
            stmt.setString(5, change.comment().orElse(null));
            stmt.setTimestamp(6, Timestamp.valueOf(change.changedAt()));

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException(
                String.format("Failed to log status change for paper %s", change.paperId()),
                ex
            );
        }
    }

    @Override
    public List<StatusChange> findAllByPaper(final UUID paperId) {
        final String sql =  """
                SELECT id, paper_id, old_status, new_status, changed_by, comment, changed_at
                FROM status_history
                WHERE paper_id = ?
                ORDER BY changed_at DESC
                """;

        try (Connection conn = this.pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, Objects.requireNonNull(paperId));
            
            try (ResultSet rs = stmt.executeQuery()) {
                final List<StatusChange> history = new ArrayList<>();
                while (rs.next()) {
                    history.add(this.map(rs));
                }
                return history;
            }
        } catch (SQLException ex) {
            throw new RepositoryException(
                String.format("Error: %s", paperId),
                ex
            );
        }
    }

    private StatusChange map(final ResultSet rs) throws SQLException {
        final String oldStatusRaw = rs.getString("old_status");
        final String commentRaw = rs.getString("comment");

        return new StatusChange(
            rs.getObject("id", UUID.class), 
            rs.getObject("paper_id", UUID.class),
            oldStatusRaw == null ? null : PaperStatus.valueOf(oldStatusRaw),
            PaperStatus.valueOf(rs.getString("new_status")),
            rs.getObject("changed_by", UUID.class),
            commentRaw,
            rs.getObject("changed_at", LocalDateTime.class)
        );
    }
}
