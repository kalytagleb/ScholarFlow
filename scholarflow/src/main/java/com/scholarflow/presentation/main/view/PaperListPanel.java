package com.scholarflow.presentation.main.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.main.models.PaperTableModel;

public final class PaperListPanel extends JPanel {
    private final Translator translator;
    private final JTable table;
    private final JLabel title;

    public PaperListPanel(final PaperTableModel model, final Translator translator) {
        this.translator = translator;
        this.title = new JLabel(translator.translate("dashboard.papers_list"));
        this.table = new JTable(model);

        this.setLayout(new BorderLayout(0, 20));
        this.setBackground(Color.WHITE);
        this.setBorder(new EmptyBorder(30, 30, 30, 30));

        this.setupUI();
    }

    private void setupUI() {
        this.title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        this.title.setForeground(new Color(44, 62, 80));
        this.add(this.title, BorderLayout.NORTH);

        this.table.setRowHeight(35);
        this.table.setShowVerticalLines(false);
        this.table.setGridColor(new Color(236, 240, 241));

        this.table.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer(translator));

        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(236, 240, 241)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    public int selectionIndex() {
        return this.table.getSelectedRow();
    }

    public void adapt(Translator newTranslator) {
        this.title.setText(newTranslator.translate("dashboard.papers_list"));
        this.table.repaint();
    }
}
