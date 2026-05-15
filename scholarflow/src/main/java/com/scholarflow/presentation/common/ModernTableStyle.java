package com.scholarflow.presentation.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public final class ModernTableStyle {
    private final Color headerBg = new Color(248, 249, 250);
    private final Color selectionBg = new Color(232, 244, 253);
    private final Color gridColor = new Color(240, 240, 240);

    public void apply(JTable table) {
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setGridColor(gridColor);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(selectionBg);
        table.setSelectionForeground(Color.BLACK);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setBackground(headerBg);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 2) {
                table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }
        }
    }
}
