package com.scholarflow;

import javax.swing.SwingUtilities;

import com.scholarflow.business.service.UserService;
import com.scholarflow.data.connection.DatabaseConfig;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.jdbc.JdbcUserRepository;
import com.scholarflow.presentation.login.LoginFrame;

public final class Main {
    public static void main(String[] args) {
        try {
            DatabaseConfig config = new DatabaseConfig("config/database.properties");

            DatabaseConnectionPool pool = new DatabaseConnectionPool(config);

            JdbcUserRepository userRepository = new JdbcUserRepository(pool);
            UserService userService = new UserService(userRepository);

            SwingUtilities.invokeLater(() -> {
                new LoginFrame(userService).open();
            });
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}