package com.scholarflow.presentation.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public final class SecondaryButton extends JButton {
    public SecondaryButton(String text) {
        super(text);
        this.setOpaque(true);
        this.setContentAreaFilled(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        this.setBackground(new Color(236, 240, 241));
        this.setForeground(new Color(44, 62, 80));
        this.setFocusPainted(false);
        this.setBorderPainted(false);
        this.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setMargin(new Insets(5, 15, 5, 15));
    }
}
