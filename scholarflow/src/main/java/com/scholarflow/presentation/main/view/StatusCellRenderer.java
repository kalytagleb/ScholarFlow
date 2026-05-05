package com.scholarflow.presentation.main.view;

import com.scholarflow.business.model.enums.PaperStatus;
import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.common.StatusBadge;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public final class StatusCellRenderer implements TableCellRenderer {
    private final Translator translator;

    public StatusCellRenderer(final Translator translator) {
        this.translator = translator;
    }

    @Override
    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    ) {
        if (value instanceof PaperStatus status) {
            String text = translator.translate("status." + status.name().toLowerCase());

            StatusBadge badge = new StatusBadge(status, text);

            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            wrapper.add(badge);

            return wrapper;
        }

        return new JLabel(value != null ? value.toString() : "");
    }
}
