package com.scholarflow.data.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.Review;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.exception.RepositoryException;
import com.scholarflow.data.repository.ReviewRepository;

public final class JdbcReviewRepository implements ReviewRepository {
    private final DatabaseConnectionPool pool;

    public JdbcReviewRepository(final DatabaseConnectionPool pool) {
        this.pool = Objects.requireNonNull(pool);
    }

    @Override
    public Review save(final Review review) {
        final String sql = """
                INSERT INTO reviews (assignment_id, decision, comments, private_notes)
                VALUES (?, ?, ?, ?)
                RETURNING *
                """;

        try (Connection conn = pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, review.assignmentId());
            stmt.setString(2, review.decision().name());
            stmt.setString(3, review.comments());
            stmt.setString(4, review.privateNotes().orElse(null));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return this.map(rs);
                }
                throw new RepositoryException("Failed to save review");
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error saving review for assignment: " + review.assignmentId(), ex);
        }
    }

    @Override
    public Optional<Review> findById(final UUID id) {
        final String sql = "SELECT * FROM reviews WHERE id = ?";
        return this.fetchSingle(sql, id);
    }

    @Override
    public Optional<Review> findByAssignmentId(final UUID assignmentId) {
        final String sql = "SELECT * FROM reviews WHERE assignment_id = ?";
        return this.fetchSingle(sql, assignmentId);
    }

    @Override
    public void delete(final UUID id) {
        final String sql = "DELETE FROM reviews WHERE id = ?";

        try (Connection conn = pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException("Error deleting review: " + id, ex);
        }
    }

    // For decomposition
    private Optional<Review> fetchSingle(String sql, UUID param) {
        try (Connection conn = pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(this.map(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error executing query: " + sql, ex);
        }
    }

    private Review map(final ResultSet rs) throws SQLException {
        return new Review(
            rs.getObject("id", UUID.class),
            rs.getObject("assignment_id", UUID.class),
            rs.getString("decision"),
            rs.getString("comments"),
            rs.getString("private_notes"),
            rs.getObject("submitted_at", LocalDateTime.class)
        );
    }
}
