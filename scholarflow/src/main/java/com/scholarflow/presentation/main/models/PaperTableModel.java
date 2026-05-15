package com.scholarflow.presentation.main.models;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.service.Translator;

import javax.swing.table.AbstractTableModel;

public final class PaperTableModel extends AbstractTableModel implements PaperProviderModel {
    private final List<Paper> papers;
    private final Translator translator;
    private final Map<UUID, String> fieldNames;

    public PaperTableModel(
        final List<Paper> papers,
        final Translator translator,
        final Map<UUID, String> fieldNames
    ) {
        this.papers = List.copyOf(papers);
        this.translator = translator;
        this.fieldNames = Map.copyOf(fieldNames);
    }

    @Override
    public int getRowCount() {
        return this.papers.size();
    }

    @Override
    public int getColumnCount() {
        return 4; 
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> translator.translate("table.paper.title");
            case 1 -> translator.translate("table.paper.field");
            case 2 -> translator.translate("table.paper.status");
            case 3 -> translator.translate("table.paper.date");
            default -> "";
        };
    }

    @Override
    public Object getValueAt(int row, int col) {
        final Paper paper = this.papers.get(row);
        return switch (col) {
            case 0 -> paper.title();
            case 1 -> fieldNames.getOrDefault(paper.fieldId(), "Unknown");
            case 2 -> paper.status(); 
            case 3 -> paper.createdAt()
                .map(d -> d.toLocalDate().toString())
                .orElse("-");
                
            default -> "";
        };
    }

    public Paper paper(final int index) {
        return this.papers.get(index);
    }
}
