package com.scholarflow.presentation.main.models;

import javax.swing.table.TableModel;

import com.scholarflow.business.model.Paper;

public interface PaperProviderModel extends TableModel {
    Paper paper(int rowIndex);
}
