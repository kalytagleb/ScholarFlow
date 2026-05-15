package com.scholarflow.presentation.common;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.scholarflow.business.service.Translator;

public final class RoleSwitcher extends JPanel {
    private final JButton readerBtn;
    private final JButton researcherBtn;
    private String selectedRole = "READER";

    public RoleSwitcher(final Translator translator) {
        this.setLayout(new GridLayout(1, 2));
        this.setMaximumSize(new Dimension(280, 35));
        this.setOpaque(false);

        this.readerBtn = this.createButton(translator.translate("role.reader"));
        this.researcherBtn = this.createButton(translator.translate("role.researcher"));

        this.readerBtn.addActionListener(e -> this.select("READER"));
        this.researcherBtn.addActionListener(e -> this.select("RESEARCHER"));

        this.add(readerBtn);
        this.add(researcherBtn);

        this.select("READER");
    }

    private void select(final String role) {
        this.selectedRole = role;
        this.applyStyles();
    }

    private void applyStyles() {
        this.styleButton(readerBtn, "READER".equals(selectedRole));
        this.styleButton(researcherBtn, "RESEARCHER".equals(selectedRole));
    }

    private void styleButton(final JButton btn, final boolean active) {
        btn.setFocusPainted(false);
        if (active) {
            btn.setBackground(new Color(41, 128, 185));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(149, 165, 166));
        }
    }

    private JButton createButton(final String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 221, 225), 1));
        return btn;
    }

    public String currentRole() {
        return this.selectedRole;
    }
}
