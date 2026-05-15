package com.scholarflow.presentation.main.controller;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.User;
import com.scholarflow.business.service.AuditService;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.business.service.XmlService;
import com.scholarflow.presentation.main.view.AdminPanel;

public final class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final PaperService paperService;
    private final XmlService xmlService;
    private final AuditService auditService;
    private final Translator translator;
    private final User admin;
    private final AdminPanel view;

    private List<User> allUsers = Collections.emptyList();
    private List<User> filteredUsers = Collections.emptyList();

    public AdminController(
        final UserService userService,
        final PaperService paperService,
        final XmlService xmlService,
        final AuditService auditService,
        final Translator translator,
        final User admin,
        final AdminPanel view
    ) {
        this.userService = Objects.requireNonNull(userService);
        this.paperService = Objects.requireNonNull(paperService);
        this.xmlService   = Objects.requireNonNull(xmlService);
        this.auditService = Objects.requireNonNull(auditService);
        this.translator   = Objects.requireNonNull(translator);
        this.admin        = Objects.requireNonNull(admin);
        this.view         = Objects.requireNonNull(view);

        this.init();
    }

    private void init() {
        view.onSearchChange(this::applyFilter);
        view.onChangeRole(this::handleChangeRole);
        view.onDeactivate(this::handleDeactivate);
        view.onExportXml(this::handleExportXml);
        view.onImportXml(this::handleImportXml);
        this.loadUsers();
    }

    private void loadUsers() {
        new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() {
                return userService.findAllActive();
            }

            @Override
            protected void done() {
                try {
                    allUsers = get();
                    filteredUsers = allUsers;
                    view.populateUsers(allUsers);
                } catch (Exception e) {
                    log.error("Failed to load users", e);
                    JOptionPane.showMessageDialog(view, translator.translate("error.load_failed"));
                }
            }
        }.execute();
    }

    private void applyFilter() {
        final String query = view.searchText().trim();
        if (query.isEmpty()) {
            filteredUsers = allUsers;
            view.populateUsers(allUsers);
            return;
        }

        try {
            final Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
            filteredUsers = allUsers.stream()
                .filter(u -> pattern.matcher(u.fullName()).find()
                    || pattern.matcher(u.username()).find()
                    || pattern.matcher(u.email()).find())
                .collect(Collectors.toList());
        } catch (PatternSyntaxException e) {
            final String lc = query.toLowerCase();
            filteredUsers = allUsers.stream()
                .filter(u -> u.fullName().toLowerCase().contains(lc)
                    || u.username().toLowerCase().contains(lc))
                .collect(Collectors.toList());
        }

        view.populateUsers(filteredUsers);
    }

    private void handleChangeRole() {
        final int row = view.selectedRow();
        if (row < 0 || row >= filteredUsers.size()) {
            JOptionPane.showMessageDialog(view, translator.translate("admin.select_user_first"));
            return;
        }

        final User target = filteredUsers.get(row);
        final String[] roles = {"READER", "RESEARCHER", "REVIEWER", "ADMIN"};

        final String newRole = (String) JOptionPane.showInputDialog(
            view,
            translator.translate("admin.select_role"),
            translator.translate("admin.change_role"),
            JOptionPane.QUESTION_MESSAGE,
            null,
            roles,
            target.role()
        );

        if (newRole == null || newRole.equals(target.role())) return;

        try {
            userService.changeRole(target.id().orElseThrow(), newRole, admin);
            auditService.log(
                admin.id().orElseThrow(),
                "ROLE_CHANGE",
                "USER",
                target.id().orElseThrow(),
                "Role changed from " + target.role() + " to " + newRole
            );
            loadUsers();
        } catch (Exception e) {
            log.error("Role change failed for user {}", target.id(), e);
            JOptionPane.showMessageDialog(view, e.getMessage());
        }
    }

    private void handleDeactivate() {
        final int row = view.selectedRow();
        if (row < 0 || row >= filteredUsers.size()) {
            JOptionPane.showMessageDialog(view, translator.translate("admin.select_user_first"));
            return;
        }

        final User target = filteredUsers.get(row);

        final int confirm = JOptionPane.showConfirmDialog(
            view,
            translator.translate("admin.confirm_deactivate") + " " + target.fullName() + "?",
            translator.translate("admin.deactivate"),
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            userService.deactivateUser(target.id().orElseThrow());
            auditService.log(
                admin.id().orElseThrow(),
                "USER_DEACTIVATED",
                "USER",
                target.id().orElseThrow(),
                "Deactivated by admin"
            );
            loadUsers();
        } catch (Exception e) {
            log.error("Failed to deactivate user {}", target.id(), e);
            JOptionPane.showMessageDialog(view, e.getMessage());
        }
    }

    private void handleExportXml() {
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
        chooser.setSelectedFile(new File("accepted_papers.xml"));

        if (chooser.showSaveDialog(view) != JFileChooser.APPROVE_OPTION) return;

        final File target = chooser.getSelectedFile();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                final List<Paper> accepted = paperService.findPublishedPapers();
                xmlService.exportPapers(accepted, target);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(view, translator.translate("admin.export_success"));
                } catch (Exception e) {
                    log.error("XML export failed", e);
                    final String cause = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    JOptionPane.showMessageDialog(view, translator.translate("admin.export_failed") + ": " + cause);
                }
            }
        }.execute();
    }

    private void handleImportXml() {
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));

        if (chooser.showOpenDialog(view) != JFileChooser.APPROVE_OPTION) return;

        final File source = chooser.getSelectedFile();

        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                return xmlService.importFields(source).size();
            }

            @Override
            protected void done() {
                try {
                    final int count = get();
                    JOptionPane.showMessageDialog(
                        view,
                        String.format(translator.translate("admin.import_success"), count)
                    );
                } catch (Exception e) {
                    log.error("XML import failed", e);
                    final String cause = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    JOptionPane.showMessageDialog(view, translator.translate("admin.import_failed") + ": " + cause);
                }
            }
        }.execute();
    }
}
