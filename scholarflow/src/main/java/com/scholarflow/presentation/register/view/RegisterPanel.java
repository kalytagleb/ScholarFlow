package com.scholarflow.presentation.register.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.scholarflow.presentation.common.PrimaryButton;

public final class RegisterPanel extends JPanel {
    private final JTextField usernameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField fullNameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    // Choose role: READER or RESEARCHER
    private final JComboBox<String> roleCombo = new JComboBox<>(new String[]{"READER", "RESEARCHER"});
    // Choose scientific field (filled from DB)
    private final JComboBox<String> fieldCombo = new JComboBox<>();

    private final PrimaryButton registerBtn = new PrimaryButton("Create Account");
    private final JLabel errorLabel = new JLabel(" ", SwingConstants.CENTER);

    public RegisterPanel() {
        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(245, 246, 250));
        this.setupLayout();
    }

    private void setupLayout() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));

        JLabel title = new JLabel("Join Scholarflow");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        styleField(usernameField);
        styleField(emailField);
        styleField(fullNameField);
        styleField(passwordField);

        roleCombo.setMaximumSize(new Dimension(280, 35));
        fieldCombo.setMaximumSize(new Dimension(280, 35));

        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        addField(card, "Username", usernameField);
        addField(card, "Email", emailField);
        addField(card, "Full Name", fullNameField);
        addField(card, "Password", passwordField);
        addField(card, "I am a: ", roleCombo);
        addField(card, "Scientific Field: ", fieldCombo);

        // card.add(new JLabel("I am a:"));
        // card.add(roleCombo);
        // card.add(Box.createRigidArea(new Dimension(0, 15)));

        // card.add(new JLabel("Scientific Field:"));
        // card.add(fieldCombo);

        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(errorLabel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(registerBtn);

        this.add(card, new GridBagConstraints());
    }

    // To add title above field
    private void addField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(127, 140, 141));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(280, 35));
        field.setPreferredSize(new Dimension(280, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    // Methods for controller
    public String selectedFieldName() {
        return (String) fieldCombo.getSelectedItem();
    }

    public void setFieldNames(List<String> names) {
        // Remove all items in order to list would be clear.
        fieldCombo.removeAllItems();
        for (String name : names) {
            fieldCombo.addItem(name);
        }
    }

    public String username() { return usernameField.getText(); }
    public String email() { return emailField.getText(); }
    public String fullName() { return fullNameField.getText(); }
    public String password() { return new String(passwordField.getPassword()); }
    public String role() { return (String) roleCombo.getSelectedItem(); }

    public void onRegister(Runnable action) {
        registerBtn.addActionListener(e -> action.run());
    }

    public void displayError(String msg) {
        errorLabel.setText(msg);
    }

    // Button block (in order to user don't spam when do request to DB)
    public void setLock(boolean locked) {
        registerBtn.setEnabled(!locked);
    }
}
