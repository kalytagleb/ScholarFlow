package com.scholarflow.data.jdbc;

import java.lang.classfile.ClassFile.Option;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.User;
import com.scholarflow.data.connection.DatabaseConnection;
import com.scholarflow.data.repository.UserRepository;
import com.scholarflow.data.exception.*;

public final class JdbcUserRepository implements UserRepository {
    private final DatabaseConnection db;

    public JdbcUserRepository(final DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Optional<User> findById(final UUID id) {
        final String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = db.connection();
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
                String.format("Error of user search by id: %s", id), ex
            );
        }
    }
    
    @Override
    public Optional<User> findByUsername(final String username) {
        final String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = db.connection(); 
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(this.map(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RepositoryException(
                String.format("Error of user search by username: %s", username), ex
            );
        }
    }

    @Override
    public List<User> findAllActive() {
        final String sql = "SELECT * FROM users WHERE is_active = true ORDER BY full_name";

        final List<User> users = new ArrayList<>();

        try (Connection conn = db.connection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(this.map(rs));
            }
            return users;
        } catch (SQLException ex) {
            throw new RepositoryException(
                "Error of getting active users", ex
            );
        }
    }

    @Override
    public List<User> findByRole(final String role) {
        final String sql = "SELECT * FROM users WHERE role = ? AND is_active = true ORDER BY full_name";

        final List<User> users = new ArrayList<>();

        try (Connection conn = db.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(this.map(rs));
                }
            }
            return users;
        } catch (SQLException ex) {
            throw new RepositoryException(
                "Error of getting user by role", ex
            );
        }
    }

    @Override
    public User save(final User user) {
        final String sql = """
                INSERT INTO users (username, password_hash, email, full_name, role, field_id)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING *
                """;

        try (Connection conn = db.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.username());
            stmt.setString(2, user.passwordHash());
            stmt.setString(3, user.email());
            stmt.setString(4, user.fullName());
            stmt.setString(5, user.role());
            stmt.setObject(6, user.fieldId());

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return this.map(rs);
            } 
        } catch (SQLException ex) {
            throw new RepositoryException(
                String.format("Error of user save: %s", user.username()), ex
            );
        }
    }

    @Override
    public void update(final User user) {
        final String sql = """
                UPDATE users
                SET email = ?, full_name = ?, role = ?, field_id = ?
                WHERE id = ?
                """;
        
        try (Connection conn = db.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.email());
            stmt.setString(2, user.fullName());
            stmt.setString(3, user.role());
            stmt.setObject(4, user.fieldId());
            stmt.setObject(5, user.id());
        } catch (SQLException ex) {
            throw new RepositoryException(
                String.format("Error of user update: %s", user.id()), ex
            );
        }
    }

    @Override
    public void deactivate(final UUID id) {
        final String sql = "UPDATE users SET is_active = false WHERE id = ?";

        try (Connection conn = db.connection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException(
                String.format("Error of user deactivation: %s", id), ex
            );
        }
    }

    private User map(final ResultSet rs) throws SQLException {
        return new User(
            rs.getObject("id", UUID.class),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("email"),
            rs.getString("full_name"),
            rs.getString("role"),
            rs.getObject("field_id", UUID.class),
            rs.getBoolean("is_active"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
