package com.scholarflow.data.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.Field;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.exception.RepositoryException;
import com.scholarflow.data.repository.FieldRepository;

public final class JdbcFieldRepository implements FieldRepository {
    private final DatabaseConnectionPool pool;

    public JdbcFieldRepository(final DatabaseConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public Optional<Field> findById(final UUID id) {
        final String sql = "SELECT id, name_en, name_sk, description FROM fields WHERE id = ?";

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
            throw new RepositoryException(
                "Error finding field by id: " + id, ex
            );
        }
    }

    @Override
    public Optional<Field> findByName(final String nameEn, final String nameSk) {
        final String sql = "SELECT * FROM fields WHERE LOWER(name_en) = LOWER(?) OR LOWER(name_sk) = LOWER(?)";

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nameEn);
            stmt.setString(2, nameSk);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(this.map(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error searching field by names", ex);
        }
    }

    @Override
    public List<Field> findAll() {
        final String sql = "SELECT * FROM fields ORDER BY name_en";

        final List<Field> fields = new ArrayList<>();

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                fields.add(this.map(rs));
            }
            return fields;
        } catch (SQLException ex) {
            throw new RepositoryException(
                "Error fetching all fields", ex
            );
        }
    }

    @Override
    public Field save(final Field field) {
        final String sql = """
                INSERT INTO fields (name_en, name_sk, description)
                VALUES (?, ?, ?)
                RETURNING *
                """;

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, field.nameEn());
            stmt.setString(2, field.nameSk());
            stmt.setString(3, field.description().orElse(null));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return this.map(rs);
                }
                throw new RepositoryException("Insert failed, no ID returned");
            } 
        } catch (SQLException ex) {
            throw new RepositoryException(
                "Error saving field: " + field.nameEn(), ex
            );
        }
    }

    @Override
    public void update(final Field field) {
        if (field.id().isEmpty()) {
            throw new RepositoryException("Cannot update field without ID");
        }
        final String sql = """
                UPDATE fields
                SET name_en = ?, name_sk = ?, description = ?
                WHERE id = ?
                """;
        
        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, field.nameEn());
            stmt.setString(2, field.nameSk());
            stmt.setString(3, field.description().orElse(null));
            stmt.setObject(4, field.id().get());
            
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException(
                "Error updating field: " + field.id().get(), ex
            );
        }
    }

    @Override
    public void delete(final UUID id) {
        final String sql = "DELETE FROM fields WHERE id = ?";

        try (Connection conn = pool.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException(
                "Error deleting field: " + id, ex
            );
        }
    }

    private Field map(final ResultSet rs) throws SQLException {
        return new Field(
            rs.getObject("id", UUID.class),
            rs.getString("name_en"),
            rs.getString("name_sk"),
            rs.getString("description")
        );
    }
}
