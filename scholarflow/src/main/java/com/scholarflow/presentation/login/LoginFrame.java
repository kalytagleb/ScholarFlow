package com.scholarflow.presentation.login;

import javax.swing.JFrame;

import com.scholarflow.business.service.UserService;

public final class LoginFrame {
    private final JFrame frame;

    public LoginFrame(final UserService userService) {
        this.frame = new JFrame("Scholarflow - Sign In");

        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(400, 500);

        this.frame.setLocationRelativeTo(null);

        final LoginPanel panel = new LoginPanel();
        new LoginController(userService, panel, frame);

        this.frame.add(panel);
    }

    public void open() {
        this.frame.setVisible(true);
    }
}
