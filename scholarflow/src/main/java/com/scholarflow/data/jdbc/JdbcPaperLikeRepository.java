package com.scholarflow.data.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import com.scholarflow.business.model.PaperLike;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.exception.RepositoryException;
import com.scholarflow.data.repository.PaperLikeRepository;

public final class JdbcPaperLikeRepository implements PaperLikeRepository {
    private final DatabaseConnectionPool pool;

    public JdbcPaperLikeRepository(final DatabaseConnectionPool pool) {
        this.pool = Objects.requireNonNull(pool);
    }

    @Override
    public void save(final PaperLike like) {
        // I use here ON CONFLICT DO NOTHING in order to don't fall with error, when reader clicks twice
        final String sql = """
            INSERT INTO paper_likes (paper_id, user_id)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
            """;

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, like.paperId());
            stmt.setObject(2, like.userId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException("Error saving like", ex);
        }
    }

    @Override
    public void delete(final UUID paperId, final UUID userId) {
        final String sql = "DELETE FROM paper_likes WHERE paper_id = ? AND user_id = ?";

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, paperId);
            stmt.setObject(2, userId);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException("Error removing like", ex);
        }
    }

    @Override
    public long countByPaper(final UUID paperId) {
        final String sql = "SELECT COUNT(*) FROM paper_likes WHERE paper_id = ?";

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, paperId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error counting likes", ex);
        }
    }

    @Override
    public boolean exists(final UUID paperId, final UUID userId) {
        final String sql = "SELECT 1 FROM paper_likes WHERE paper_id = ? AND user_id = ?";

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, paperId);
            stmt.setObject(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error existence", ex);
        }
    }
}
