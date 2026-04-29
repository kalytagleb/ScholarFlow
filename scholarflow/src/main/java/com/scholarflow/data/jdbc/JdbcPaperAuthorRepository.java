package com.scholarflow.data.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.scholarflow.business.model.PaperAuthor;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.exception.RepositoryException;
import com.scholarflow.data.repository.PaperAuthorRepository;

public final class JdbcPaperAuthorRepository implements PaperAuthorRepository {
    private final DatabaseConnectionPool pool;
    
    public JdbcPaperAuthorRepository(final DatabaseConnectionPool pool) {
        this.pool = Objects.requireNonNull(pool);
    }

    @Override
    public void save(final PaperAuthor author) {
        final String sql = """
                INSERT INTO paper_authors (paper_id, user_id, author_order)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, author.paperId());
            stmt.setObject(2, author.userId());
            stmt.setInt(3, author.order());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException("Error saving paper author", ex);
        }
    }

    @Override
    public List<PaperAuthor> findByPaper(final UUID paperId) {
        final String sql = "SELECT * FROM paper_authors WHERE paper_id = ? ORDER BY author_order ASC";
        final List<PaperAuthor> results = new ArrayList<>();

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, paperId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(this.map(rs));
                }
            }

            return results;
        } catch (SQLException ex) {
            throw new RepositoryException("Error saving paper author", ex);
        }
    }

    @Override
    public List<PaperAuthor> findByUser(final UUID userId) {
        final String sql = "SELECT * FROM paper_authors WHERE user_id = ?";
        final List<PaperAuthor> results = new ArrayList<>();

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(this.map(rs));
                }
            }

            return results;
        } catch (SQLException ex) {
            throw new RepositoryException("Error saving paper author", ex);
        }
    }

    @Override
    public void remove(final UUID paperId, final UUID userId) {
        final String sql = "DELETE FROM paper_authors WHERE paper_id = ? AND user_id = ?";

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, paperId);
            stmt.setObject(2, userId);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException("Error saving paper author", ex);
        }
    }

    @Override
    public void removeAllByPaper(final UUID paperId) {
        final String sql = "DELETE FROM paper_authors WHERE paper_id = ?";

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, paperId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException("Error saving paper author", ex);
        }
    }

    private PaperAuthor map(final ResultSet rs) throws SQLException {
        return new PaperAuthor(
            rs.getObject("paper_id", UUID.class), 
            rs.getObject("user_id", UUID.class),
            rs.getInt("author_order")
        );
    }
}
