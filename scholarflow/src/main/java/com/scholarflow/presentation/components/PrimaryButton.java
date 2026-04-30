package com.scholarflow.presentation.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;

public final class PrimaryButton extends JButton {
    public PrimaryButton(final String text) {
        super(text);
        this.setContentAreaFilled(false);
        this.setOpaque(true);
        this.setBackground(new Color(41, 128, 185));
        this.setForeground(Color.WHITE);
        this.setFocusPainted(false);
        this.setBorderPainted(false);
        this.setFont(new Font("Segoe UI", Font.BOLD, 14));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setMaximumSize(new Dimension(300, 40));
    }
}
