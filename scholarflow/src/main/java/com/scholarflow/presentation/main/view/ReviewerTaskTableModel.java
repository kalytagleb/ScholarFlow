package com.scholarflow.presentation.main.view;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.swing.table.AbstractTableModel;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.ReviewAssignment;
import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.main.models.PaperProviderModel;

public final class ReviewerTaskTableModel extends AbstractTableModel implements PaperProviderModel {
    private final List<Paper> papers;
    private final List<ReviewAssignment> assignments;
    private final Translator tr;
    private final Map<UUID, String> fieldNames;

    public ReviewerTaskTableModel(
        final List<Paper> papers, 
        final List<ReviewAssignment> assignments,
        final Translator tr,
        final Map<UUID, String> fieldNames
    ) {
        this.papers = List.copyOf(Objects.requireNonNull(papers));
        this.assignments = List.copyOf(Objects.requireNonNull(assignments));
        this.tr = Objects.requireNonNull(tr);
        this.fieldNames = Map.copyOf(Objects.requireNonNull(fieldNames));
    }

    @Override
    public int getRowCount() {
        return papers.size();
    }

    @Override
    public int getColumnCount() {
        return 4; 
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> tr.translate("table.paper.title");
            case 1 -> tr.translate("table.paper.field");
            case 2 -> tr.translate("table.paper.deadline");
            case 3 -> tr.translate("table.paper.status");
            default -> "";
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final Paper paper = papers.get(rowIndex);
        final ReviewAssignment assignment = assignments.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> paper.title();
            case 1 -> fieldNames.getOrDefault(paper.fieldId(), "Unknown");
            case 2 -> assignment.deadline().toString(); 
            case 3 -> paper.status(); 
            default -> null;
        };
    }

    public Paper paper(int row) {
        return papers.get(row);
    }
}
