package com.scholarflow.presentation.login.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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

public final class LoginPanel extends JPanel {
    private final JLabel titleLabel = new JLabel("Scholarflow");
    private final JLabel userLabel = new JLabel();
    private final JLabel passLabel = new JLabel();

    // Create components
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final PrimaryButton loginBtn;
    private final JLabel errorLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JButton registerLink;

    // Language choice: EN or SK
    private final JComboBox<String> langCombo = new JComboBox<>(new String[]{"English", "Slovenčina"});

    public LoginPanel(final Translator translator) {
        this.loginBtn = new PrimaryButton(translator.translate("login.signin"));
        this.registerLink = new JButton(translator.translate("login.register_link"));

        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(245, 246, 250));
        this.setupLayout();

        this.updateTexts(translator);
    }

    public void updateTexts(Translator translator) {
        titleLabel.setText(translator.translate("login.title"));
        userLabel.setText(translator.translate("login.username"));
        passLabel.setText(translator.translate("login.password"));
        loginBtn.setText(translator.translate("login.signin"));
        registerLink.setText(translator.translate("login.register_link"));

        langCombo.setSelectedItem(translator.currentLanguage().equals("sk") ? "Slovenčina" : "English");
    }

    private void setupLayout() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));

        langCombo.setMaximumSize(new Dimension(120, 25));
        langCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Scholarflow");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(44, 62, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.styleInputField(usernameField);
        this.styleInputField(passwordField);

        card.add(langCombo);
        card.add(Box.createRigidArea(new Dimension(0,20)));
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0,30)));

        this.addLabeledField(card, userLabel, usernameField);
        this.addLabeledField(card, passLabel, passwordField);

        card.add(errorLabel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(loginBtn);

        this.styleLinkButton(registerLink);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(registerLink);

        this.add(card, new GridBagConstraints());
    }

    private void addLabeledField(JPanel panel, JLabel label, JComponent field) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(127, 140, 141));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
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

    private void styleLinkButton(JButton btn) {
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(new Color(41, 128, 185));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    // In order to controller had access to Panel
    public String username() { return usernameField.getText(); }
    public String password() { return new String(passwordField.getPassword()); }
    public String selectedLanguage() {
        return "Slovenčina".equals(langCombo.getSelectedItem()) ? "sk" : "en";
    }

    public void onLogin(Runnable action) {
        loginBtn.addActionListener(e -> action.run());
    }

    public void onRegisterNavigate(Runnable action) {
        registerLink.addActionListener(e -> action.run());
    }

    public void onLanguageChange(Runnable action) {
        langCombo.addActionListener(e -> action.run());
    }

    public void displayError(String msg) {
        errorLabel.setText(msg);
    }

    // Button block (in order to user don't spam when do request to DB)
    public void setLock(boolean locked) {
        loginBtn.setEnabled(!locked);
    }
}
