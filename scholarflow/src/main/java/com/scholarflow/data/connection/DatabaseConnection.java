package com.scholarflow.data.connection;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public final class DatabaseConnection {
    private final DatabaseConfig config;

    /**
     * @param config configuration of connection to DB
     */
    public DatabaseConnection(final DatabaseConfig config) {
        this.config = config;
    }

    public Connection connection() throws SQLException {
        return DriverManager.getConnection(
            this.config.url(),
            this.config.username(),
            this.config.password()
        );
    }
}