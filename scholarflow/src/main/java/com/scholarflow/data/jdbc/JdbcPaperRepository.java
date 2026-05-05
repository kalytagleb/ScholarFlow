package com.scholarflow.data.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.enums.PaperStatus;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.exception.RepositoryException;
import com.scholarflow.data.repository.PaperRepository;

public final class JdbcPaperRepository implements PaperRepository {
    private final DatabaseConnectionPool pool;

    public JdbcPaperRepository(final DatabaseConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public Optional<Paper> findById(final UUID id) {
        final String sql = "SELECT * FROM papers WHERE id = ?";

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
            throw new RepositoryException("Error finding paper by id: " + id, ex);
        }
    }

    @Override
    public List<Paper> findAll() {
        final String sql = "SELECT * FROM papers ORDER BY created_at DESC";
        return this.fetchList(sql, null);
    }

    @Override
    public List<Paper> findAllBySubmitter(final UUID submitterId) {
        final String sql = "SELECT * FROM papers WHERE submitter_id = ? ORDER BY created_at DESC";
        return this.fetchList(sql, submitterId);
    }

    @Override
    public List<Paper> findAllByStatus(final PaperStatus status) {
        final String sql = "SELECT * FROM papers WHERE status = ? ORDER BY created_at DESC";
        return this.fetchList(sql, status.name());
    }

    @Override
    public Paper save(final Paper paper) {
        final String sql = """
                INSERT INTO papers (title, abstract, keywords, doi, field_id, submitter_id, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING *
                """;
        
        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paper.title());
            stmt.setString(2, paper.paperAbstract());
            stmt.setString(3, paper.keywords().orElse(null));
            stmt.setString(4, paper.doi().orElse(null));
            stmt.setObject(5, paper.fieldId());
            stmt.setObject(6, paper.submitterId());
            stmt.setString(7, paper.status().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return this.map(rs);
                }
                throw new RepositoryException("Failed to save paper");
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error saving paper: " + paper.title(), ex);
        }
    }

    @Override
    public void update(final Paper paper) {
        final String sql = """
            UPDATE papers 
            SET title = ?, abstract = ?, keywords = ?, doi = ?, status = ?, updated_at = NOW()
            WHERE id = ?
            """;
        try (Connection conn = pool.connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paper.title());
            stmt.setString(2, paper.paperAbstract());
            stmt.setString(3, paper.keywords().orElse(null));
            stmt.setString(4, paper.doi().orElse(null));
            stmt.setString(5, paper.status().name());
            stmt.setObject(6, paper.id().orElseThrow(() -> 
                new RepositoryException("Cannot update paper without ID")));

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException("Error updating paper: " + paper.title(), ex);
        }
    }

    private List<Paper> fetchList(String sql, Object param) {
        final List<Paper> results = new ArrayList<>();

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
            throw new RepositoryException("Error fetching papers list", ex);
        }
    }

    private Paper map(final ResultSet rs) throws SQLException {
        return new Paper(
            rs.getObject("id", UUID.class),
            rs.getString("title"),
            rs.getString("abstract"),
            rs.getString("keywords"),
            rs.getString("doi"),
            rs.getObject("field_id", UUID.class),
            rs.getObject("submitter_id", UUID.class),
            PaperStatus.valueOf(rs.getString("status")),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}
