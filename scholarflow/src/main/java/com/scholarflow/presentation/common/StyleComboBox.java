package com.scholarflow.presentation.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;

public final class StyleComboBox<T> extends JComboBox<T> {
    public StyleComboBox(T[] items) {
        super(items);
        this.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(new Color(220, 221, 225), 1));
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}
