package com.scholarflow.presentation.login.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.scholarflow.presentation.common.PrimaryButton;

public final class LoginPanel extends JPanel {
    // Create components
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final PrimaryButton loginBtn = new PrimaryButton("Sign In");
    private final JLabel errorLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JButton registerLink = new JButton("Don't have an account? Register here");

    public LoginPanel() {
        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(245, 246, 250));
        this.setupLayout();
    }

    private void setupLayout() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel title = new JLabel("Scholarflow");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(44, 62, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.styleInputField(usernameField);
        this.styleInputField(passwordField);

        JLabel userLabel = createFieldLabel("Username");
        JLabel passLabel = createFieldLabel("Password");

        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setForeground(new Color(41, 128, 185));
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 40)));
        card.add(userLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(usernameField);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(passLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(passwordField);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(errorLabel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(registerLink);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        this.add(card, gbc);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(127, 140, 141));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void styleInputField(JTextField field) {
        field.setMaximumSize(new Dimension(280, 35));
        field.setPreferredSize(new Dimension(280, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    // In order to controller had access to Panel
    public String username() { return usernameField.getText(); }
    public String password() { return new String(passwordField.getPassword()); }

    public void onLogin(Runnable action) {
        loginBtn.addActionListener(e -> action.run());
    }

    public void onRegisterNavigate(Runnable action) {
        registerLink.addActionListener(e -> action.run());
    }

    public void displayError(String msg) {
        errorLabel.setText(msg);
    }

    // Button block (in order to user don't spam when do request to DB)
    public void setLock(boolean locked) {
        loginBtn.setEnabled(!locked);
    }
}
