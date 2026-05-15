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

import com.scholarflow.business.model.PaperComment;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.exception.RepositoryException;
import com.scholarflow.data.repository.PaperCommentRepository;

public final class JdbcPaperCommentRepository implements PaperCommentRepository {
    private final DatabaseConnectionPool pool;

    public JdbcPaperCommentRepository(final DatabaseConnectionPool pool) {
        this.pool = Objects.requireNonNull(pool);
    }

    @Override
    public void save(final PaperComment comment) {
        final String sql = """
            INSERT INTO paper_comments (paper_id, user_id, content)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, comment.paperId());
            stmt.setObject(2, comment.userId());
            stmt.setString(3, comment.content());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RepositoryException("Error saving comment for paper", ex);
        }
    }

    @Override
    public List<PaperComment> findByPaper(final UUID paperId) {
        final String sql = """
                SELECT c.*, u.full_name
                FROM paper_comments c
                JOIN users u ON c.user_id = u.id
                WHERE c.paper_id = ?
                ORDER BY c.created_at ASC
                """;
        final List<PaperComment> comments = new ArrayList<>();

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, paperId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(this.map(rs));
                }
            }

            return comments;
        } catch (SQLException ex) {
            throw new RepositoryException("Error fetching comments for paper", ex);
        }
    }

    @Override
    public void delete(final UUID id) {
        final String sql = "DELETE FROM paper_comments WHERE id = ?";

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException("Error deleting comment", ex);
        }
    }

    private PaperComment map(final ResultSet rs) throws SQLException {
        return new PaperComment(
            rs.getObject("id", UUID.class),
            rs.getObject("paper_id", UUID.class), 
            rs.getObject("user_id", UUID.class),
            rs.getString("full_name"),
            rs.getString("content"),
            rs.getObject("created_at", LocalDateTime.class)
        );
    }
}
