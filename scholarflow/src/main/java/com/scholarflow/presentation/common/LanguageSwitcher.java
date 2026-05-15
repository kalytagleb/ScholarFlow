package com.scholarflow.presentation.common;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;

public final class LanguageSwitcher extends JPanel {
    private final JButton enBtn;
    private final JButton skBtn;
    private String currentLang;

    public LanguageSwitcher(String initialLang) {
        this.currentLang = initialLang;
        this.setLayout(new GridLayout(1, 2));
        this.setPreferredSize(new Dimension(120, 30));
        this.setMaximumSize(new Dimension(120, 30));
        this.setOpaque(false);

        enBtn = createToggleBtn("EN", "en");
        skBtn = createToggleBtn("SK", "sk");

        this.add(enBtn);
        this.add(skBtn);

        updateVisuals();
    }

    private JButton createToggleBtn(String text, String code) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateVisuals() {
        styleButton(enBtn, "en".equals(currentLang));
        styleButton(skBtn, "sk".equals(currentLang));
    }

    private void styleButton(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(new Color(41, 128, 185));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(236, 240, 241));
            btn.setForeground(new Color(127, 140, 141));
        }
    }

    public String selectedLanguage() {
        return currentLang;
    }

    public void onLanguageChange(Consumer<String> action) {
        enBtn.addActionListener(e -> {
            if (!"en".equals(currentLang)) {
                currentLang = "en";
                updateVisuals();
                action.accept("en");
            }
        });

        skBtn.addActionListener(e -> {
            if (!"sk".equals(currentLang)) {
                currentLang = "sk";
                updateVisuals();
                action.accept("sk");
            }
        });
    }
}
