package com.scholarflow.presentation.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComboBox;

public final class StyleComboBox<T> extends JComboBox<T> {
    public StyleComboBox(T[] items) {
        super(items);
        this.setMaximumSize(new Dimension(280, 35));
        this.setPreferredSize(new Dimension(280, 35));
        this.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        this.setBackground(Color.WHITE);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}
