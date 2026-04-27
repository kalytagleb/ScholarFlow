package com.scholarflow.data.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.ReviewAssignment;
import com.scholarflow.business.model.enums.AssignmentStatus;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.exception.RepositoryException;
import com.scholarflow.data.repository.ReviewAssignmentRepository;

public final class JdbcReviewAssignmentRepository implements ReviewAssignmentRepository {
    private final DatabaseConnectionPool pool;

    public JdbcReviewAssignmentRepository(final DatabaseConnectionPool pool) {
        this.pool = Objects.requireNonNull(pool);
    }

    @Override
    public ReviewAssignment save(final ReviewAssignment assignment) {
        final String sql = """
                INSERT INTO review_assignments (paper_id, reviewer_id, assigned_by, deadline)
                VALUES (?, ?, ?, ?)
                RETURNING *
                """;

        try (Connection conn = pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, assignment.paperId());
            stmt.setObject(2, assignment.reviewerId());
            stmt.setObject(3, assignment.assignedBy());
            stmt.setObject(4, assignment.deadline());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return this.map(rs);
                }
                throw new RepositoryException("Failed to save review assignment");
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error creating assignment", ex);
        }
    }

    @Override
    public Optional<ReviewAssignment> findById(final UUID id) {
        final String sql = "SELECT * FROM review_assignments WHERE id = ?";

        try (Connection conn = pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(this.map(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error finding assignment", ex);
        }
    }

    @Override
    public List<ReviewAssignment> findByPaper(final UUID paperId) {
        final String sql = "SELECT * FROM review_assignments WHERE paper_id = ?";
        return this.fetchList(sql, paperId);
    }

    @Override
    public List<ReviewAssignment> findByReviewer(final UUID reviewerId) {
        final String sql = "SELECT * FROM review_assignments WHERE reviewer_id = ?";
        return this.fetchList(sql, reviewerId);
    }

    @Override
    public void updateStatus(final UUID id, final AssignmentStatus status) {
        final String sql = "UPDATE review_assignments SET status = ? WHERE id = ?";

        try (Connection conn = pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setObject(2, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException("Error updating assignemt status", ex);
        }
    }

    @Override
    public void delete(final UUID id) {
        final String sql = "DELETE FROM review_assignments WHERE id = ?";

        try (Connection conn = pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException("Error deleting assignemt", ex);
        }
    }

    // For decomposition
    private List<ReviewAssignment> fetchList(String sql, UUID param) {
        final List<ReviewAssignment> results = new ArrayList<>();

        try (Connection conn = pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(this.map(rs));
                }
            }
            return results;
        } catch (SQLException ex) {
            throw new RepositoryException("Error fetching assignment list", ex);
        }
    }

    private ReviewAssignment map(final ResultSet rs) throws SQLException {
        return new ReviewAssignment(
            rs.getObject("id", UUID.class),
            rs.getObject("paper_id", UUID.class),
            rs.getObject("reviewer_id", UUID.class),
            rs.getObject("assigned_by", UUID.class),
            rs.getObject("assigned_at", LocalDateTime.class),
            rs.getObject("deadline", LocalDate.class),
            rs.getString("status")
        );
    }
}