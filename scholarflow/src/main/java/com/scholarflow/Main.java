package com.scholarflow;

import javax.swing.SwingUtilities;

import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.data.connection.DatabaseConfig;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.jdbc.JdbcFieldRepository;
import com.scholarflow.data.jdbc.JdbcUserRepository;
import com.scholarflow.presentation.login.view.LoginFrame;

public final class Main {
    public static void main(String[] args) {
        try {
            DatabaseConfig config = new DatabaseConfig("config/database.properties");

            DatabaseConnectionPool pool = new DatabaseConnectionPool(config);

            UserService userService = new UserService(new JdbcUserRepository(pool));
            FieldService fieldService = new FieldService(new JdbcFieldRepository(pool));

            Translator translator = new Translator("en");

            SwingUtilities.invokeLater(() -> {
                new LoginFrame(userService, fieldService, translator).open();
            });
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}