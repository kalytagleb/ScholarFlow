package com.scholarflow.presentation.main.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.scholarflow.business.model.User;
import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.common.PrimaryButton;
import com.scholarflow.presentation.common.SecondaryButton;

public final class AdminPanel extends JPanel {
    private final Translator translator;
    private final JTable userTable;
    private final DefaultTableModel tableModel;
    private final JTextField searchField = new JTextField();
    private final PrimaryButton changeRoleBtn;
    private final PrimaryButton exportBtn;
    private final PrimaryButton importBtn;

    public AdminPanel(Translator translator) {
        this.translator = translator;
        this.setLayout(new BorderLayout(0, 20));
        this.setBackground(Color.WHITE);
        this.setBorder(new EmptyBorder(30, 30, 30, 30));

        String[] columns = {
            translator.translate("admin.user"),
            translator.translate("admin.email"),
            translator.translate("admin.role")
        };

        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        this.userTable = new JTable(tableModel);
        this.changeRoleBtn = new PrimaryButton(translator.translate("admin.change_role"));
        this.exportBtn = new PrimaryButton(translator.translate("admin.export_xml"));
        this.importBtn = new PrimaryButton(translator.translate("admin.import_xml"));
 
        this.setupUI();
    }

    private void setupUI() {
        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);
        topPanel.add(new JLabel(translator.translate("admin.search")), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        this.add(topPanel, BorderLayout.NORTH);

        // Center (Table)
        this.add(new JScrollPane(userTable), BorderLayout.CENTER);

        // Footer
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        bottomPanel.setOpaque(false);

        Dimension btnSize = new Dimension(180, 40);
        changeRoleBtn.setPreferredSize(btnSize);
        exportBtn.setPreferredSize(btnSize);
        importBtn.setPreferredSize(btnSize);

        bottomPanel.add(changeRoleBtn);
        bottomPanel.add(exportBtn);
        bottomPanel.add(importBtn);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    public void accomodateUsers(List<User> users) {
        tableModel.setRowCount(0);
        for (User u : users) {
            Vector<String> row = new Vector<>();
            row.add(u.fullName());
            row.add(u.email());
            row.add(u.role());
            tableModel.addRow(row);
        }
    }

    public String searchText() {
        return searchField.getText();
    }

    public int selectedRow() {
        return userTable.getSelectedRow();
    }

    public void onSearchChange(Runnable r) {
        searchField.addActionListener(e -> r.run());
    }

    public void onChangeRole(Runnable r) {
        changeRoleBtn.addActionListener(e -> r.run());
    }

    public void onExportXml(Runnable r) {
        exportBtn.addActionListener(e -> r.run());
    }

    public void onImportXml(Runnable r) {
        importBtn.addActionListener(e -> r.run());
    }
}