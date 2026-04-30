package com.scholarflow.presentation.login;

import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.scholarflow.business.model.User;
import com.scholarflow.business.service.UserService;

public final class LoginController {
    private final UserService userService;
    private final LoginPanel view;
    private final JFrame frame;

    public LoginController(UserService userService, LoginPanel view, JFrame frame) {
        this.userService = userService;
        this.view = view;
        this.frame = frame;
        this.init();
    }

    private void init() {
        this.view.onLogin(this::handleLogin);
    }

    private void handleLogin() {
        final String name = view.username();
        final String pass = view.password();

        if (name.isBlank() || pass.isBlank()) {
            view.displayError("Username and password required");
            return;
        }

        view.setLock(true);
        view.displayError("Authenticating...");

        // We create separate thread for DB and return result into UI thread
        new SwingWorker<Optional<User>, Void>() {
            @Override
            protected Optional<User> doInBackground() {
                return userService.authenticate(name, pass);
            }

            @Override
            protected void done() {
                try {
                    Optional<User> user = get();
                    if (user.isPresent()) {
                        frame.dispose();
                        JOptionPane.showMessageDialog(null, "Welcome, " + user.get().fullName());
                    } else {
                        view.setLock(false);
                        view.displayError("Invalid username or password");
                    }
                } catch (Exception e) {
                    view.setLock(false);
                    view.displayError("Connection error.");
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
