package com.scholarflow.presentation.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public final class SecondaryButton extends JButton {
    public SecondaryButton(String text) {
        super(text);
        this.setContentAreaFilled(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        this.setForeground(new Color(127, 140, 141));
        this.setFocusPainted(false);
        this.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}
