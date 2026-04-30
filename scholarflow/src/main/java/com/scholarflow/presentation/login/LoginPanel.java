package com.scholarflow.presentation.login;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.scholarflow.presentation.components.PrimaryButton;

public final class LoginPanel extends JPanel {
    // Create components
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final PrimaryButton loginBtn = new PrimaryButton("Sign In");
    private final JLabel errorLabel = new JLabel(" ");

    public LoginPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(50, 40, 50, 40));
        this.setBackground(Color.WHITE);
        this.setupLayout();
    }

    private void setupLayout() {
        JLabel title = new JLabel("Scholarflow");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.styleField(usernameField);
        this.styleField(passwordField);

        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 40)));
        this.add(new JLabel("Username"));
        this.add(usernameField);
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        this.add(new JLabel("Password"));
        this.add(passwordField);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(errorLabel);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(loginBtn);
    }

    private void styleField(final JTextField field) {
        field.setMaximumSize(new Dimension(300, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    // In order to controller had access to Panel
    public String username() { return usernameField.getText(); }
    public String password() { return new String(passwordField.getPassword()); }

    public void onLogin(Runnable action) {
        loginBtn.addActionListener(e -> action.run());
    }

    public void displayError(String msg) {
        errorLabel.setText(msg);
    }

    // Button block (in order to user don't spam when do request to DB)
    public void setLock(boolean locked) {
        loginBtn.setEnabled(!locked);
    }
}
