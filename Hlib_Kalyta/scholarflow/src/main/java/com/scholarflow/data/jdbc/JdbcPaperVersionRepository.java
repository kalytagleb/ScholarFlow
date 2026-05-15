package com.scholarflow.data.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Optional;

import com.scholarflow.business.model.PaperVersion;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.exception.RepositoryException;
import com.scholarflow.data.repository.PaperVersionRepository;

public final class JdbcPaperVersionRepository implements PaperVersionRepository {
    private final DatabaseConnectionPool pool;

    public JdbcPaperVersionRepository(final DatabaseConnectionPool pool) {
        this.pool = Objects.requireNonNull(pool, "Connection pool cannot be null");
    }

    @Override
    public PaperVersion save(final PaperVersion version) {
        final String sql = """
                INSERT INTO paper_versions (paper_id, version_number, title, abstract, content)
                VALUES (?, ?, ?, ?, ?)
                RETURNING *
                """;

        try (Connection conn = this.pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, version.paperId());
            stmt.setInt(2, version.number());
            stmt.setString(3, version.title());
            stmt.setString(4, version.paperAbstract());
            stmt.setString(5, version.content().orElse(null));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return this.map(rs);
                }
                throw new RepositoryException("Failed to save paper version");
            }
        } catch (SQLException ex) {
            throw new RepositoryException(
                String.format("Error saving version %d for paper %s", version.number(), version.paperId()),
                ex
            );
        }
    }

    @Override
    public Optional<PaperVersion> latestOf(final UUID paperId) {
        final String sql = """
                SELECT * FROM paper_versions
                WHERE paper_id = ?
                ORDER BY version_number DESC
                LIMIT 1
                """;

        try (Connection conn = this.pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, paperId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(this.map(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RepositoryException(
                "Error fetching latest version for paper: " + paperId, ex
            );
        }
    }

    @Override
    public List<PaperVersion> allOf(final UUID paperId) {
        final String sql = "SELECT * FROM paper_versions WHERE paper_id = ? ORDER BY version_number ASC";

        try (Connection conn = this.pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, paperId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                final List<PaperVersion> versions = new ArrayList<>();
                while (rs.next()) {
                    versions.add(this.map(rs));
                }
                return versions;
            }
        } catch (SQLException ex) {
            throw new RepositoryException(
                "Error fetching version history for paper: " + paperId, ex
            );
        }
    }

    private PaperVersion map(final ResultSet rs) throws SQLException {
        return new PaperVersion(
            rs.getObject("id", UUID.class),
            rs.getObject("paper_id", UUID.class),
            rs.getInt("version_number"),
            rs.getString("title"),
            rs.getString("abstract"),
            rs.getString("content"),
            rs.getObject("created_at", LocalDateTime.class)
        );
    }
}
