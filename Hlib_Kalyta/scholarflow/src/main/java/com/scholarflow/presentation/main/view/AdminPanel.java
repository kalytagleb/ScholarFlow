package com.scholarflow.presentation.main.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.scholarflow.business.model.User;
import com.scholarflow.business.service.Translator;

public final class AdminPanel extends JPanel {
    private final Translator translator;
    private final JTextField searchField;
    private final JTable usersTable;
    private final DefaultTableModel tableModel;
    private final JButton changeRoleBtn;
    private final JButton deactivateBtn;
    private final JButton exportXmlBtn;
    private final JButton importXmlBtn;

    public AdminPanel(final Translator translator) {
        this.translator = translator;
        this.searchField = new JTextField(25);
        this.tableModel = buildTableModel();
        this.usersTable = new JTable(tableModel);
        this.changeRoleBtn = new JButton(translator.translate("admin.change_role"));
        this.deactivateBtn  = new JButton(translator.translate("admin.deactivate"));
        this.exportXmlBtn   = new JButton(translator.translate("admin.export_xml"));
        this.importXmlBtn   = new JButton(translator.translate("admin.import_xml"));

        this.setLayout(new BorderLayout(0, 15));
        this.setBackground(Color.WHITE);
        this.setBorder(new EmptyBorder(30, 30, 30, 30));
        this.setupUI();
    }

    private DefaultTableModel buildTableModel() {
        return new DefaultTableModel(
            new String[]{
                translator.translate("admin.col_name"),
                translator.translate("admin.col_username"),
                translator.translate("admin.col_email"),
                translator.translate("admin.col_role"),
                translator.translate("admin.col_active")
            },
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void setupUI() {
        final JLabel titleLabel = new JLabel(translator.translate("menu.admin_panel"));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));

        final JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.setBackground(Color.WHITE);
        final JLabel searchLabel = new JLabel(translator.translate("admin.search"));
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(260, 30));
        searchBar.add(searchLabel);
        searchBar.add(searchField);

        final JPanel north = new JPanel(new BorderLayout(0, 8));
        north.setBackground(Color.WHITE);
        north.add(titleLabel, BorderLayout.NORTH);
        north.add(searchBar, BorderLayout.SOUTH);
        this.add(north, BorderLayout.NORTH);

        usersTable.setRowHeight(30);
        usersTable.setShowVerticalLines(false);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setGridColor(new Color(236, 240, 241));
        usersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        final JScrollPane scroll = new JScrollPane(usersTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(236, 240, 241)));
        scroll.getViewport().setBackground(Color.WHITE);
        this.add(scroll, BorderLayout.CENTER);

        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        buttons.setBackground(Color.WHITE);
        buttons.add(changeRoleBtn);
        buttons.add(deactivateBtn);
        buttons.add(exportXmlBtn);
        buttons.add(importXmlBtn);
        this.add(buttons, BorderLayout.SOUTH);
    }

    public void populateUsers(final List<User> users) {
        tableModel.setRowCount(0);
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.fullName(),
                u.username(),
                u.email(),
                u.role(),
                u.isActive()
                    ? translator.translate("admin.active")
                    : translator.translate("admin.inactive")
            });
        }
    }

    public int selectedRow() {
        return usersTable.getSelectedRow();
    }

    public String searchText() {
        return searchField.getText();
    }

    public void onSearchChange(final Runnable action) {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { action.run(); }
            public void removeUpdate(DocumentEvent e) { action.run(); }
            public void changedUpdate(DocumentEvent e) { action.run(); }
        });
    }

    public void onChangeRole(final Runnable action) {
        changeRoleBtn.addActionListener(e -> action.run());
    }

    public void onDeactivate(final Runnable action) {
        deactivateBtn.addActionListener(e -> action.run());
    }

    public void onExportXml(final Runnable action) {
        exportXmlBtn.addActionListener(e -> action.run());
    }

    public void onImportXml(final Runnable action) {
        importXmlBtn.addActionListener(e -> action.run());
    }
}
