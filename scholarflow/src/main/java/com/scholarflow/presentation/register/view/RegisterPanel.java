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

import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.common.PrimaryButton;
import com.scholarflow.presentation.common.SecondaryButton;
import com.scholarflow.presentation.common.StyleComboBox;

public final class RegisterPanel extends JPanel {
    private final JLabel titleLabel = new JLabel();
    private final JLabel userLabel = new JLabel();
    private final JLabel emailLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel passLabel = new JLabel();
    private final JLabel roleChoiceLabel = new JLabel();
    private final JLabel fieldChoiceLabel = new JLabel();
    private final JLabel errorLabel = new JLabel(" ", SwingConstants.CENTER);

    private final JTextField usernameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField fullNameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    // Choose role: READER or RESEARCHER
    private final StyleComboBox<String> roleCombo = new StyleComboBox<>(new String[]{"READER", "RESEARCHER"});
    // Choose scientific field (filled from DB)
    private final StyleComboBox<String> fieldCombo = new StyleComboBox<>(new String[0]);

    private final SecondaryButton backBtn = new SecondaryButton("");
    private final PrimaryButton registerBtn = new PrimaryButton("");

    public RegisterPanel(final Translator translator) {
        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(245, 246, 250));
        this.setupLayout();
        this.updateTexts(translator);
    }

    public void updateTexts(Translator translator) {
        titleLabel.setText(translator.translate("register.title"));
        userLabel.setText(translator.translate("register.username"));
        emailLabel.setText(translator.translate("register.email"));
        nameLabel.setText(translator.translate("register.fullname"));
        passLabel.setText(translator.translate("register.password"));
        roleChoiceLabel.setText(translator.translate("register.role"));
        fieldChoiceLabel.setText(translator.translate("register.field"));
        
        registerBtn.setText(translator.translate("register.button"));
        backBtn.setText("<- " + translator.translate("login.back_to_login"));
    }

    private void setupLayout() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        this.addLabeledField(card, userLabel, usernameField);
        this.addLabeledField(card, emailLabel, emailField);
        this.addLabeledField(card, nameLabel, fullNameField);
        this.addLabeledField(card, passLabel, passwordField);
        
        this.addLabeledField(card, roleChoiceLabel, roleCombo);
        this.addLabeledField(card, fieldChoiceLabel, fieldCombo);

        card.add(errorLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(registerBtn);

        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(backBtn);
        
        // card.add(new JLabel("Scientific Field:"));
        // card.add(fieldCombo);

        this.add(card, new GridBagConstraints());
    }

    // To add title above field
    private void addLabeledField(JPanel panel, JLabel label, JComponent field) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(149, 165, 166));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (field instanceof JTextField) {
            field.setMaximumSize(new Dimension(280, 32));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
            ));
        }

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
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

    public void onBack(Runnable action) {
        backBtn.addActionListener(e -> action.run());
    }

    public void displayError(String msg) {
        errorLabel.setText(msg);
    }

    // Button block (in order to user don't spam when do request to DB)
    public void setLock(boolean locked) {
        registerBtn.setEnabled(!locked);
    }
}
