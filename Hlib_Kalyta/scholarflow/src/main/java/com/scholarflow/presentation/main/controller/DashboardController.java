package com.scholarflow.presentation.main.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.util.stream.Collectors;

import com.scholarflow.business.model.Paper;
import com.scholarflow.presentation.main.models.PaperTableModel;
import com.scholarflow.business.model.User;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.ReviewService;
import com.scholarflow.business.service.AuditService;
import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.InteractionService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.business.service.XmlService;
import com.scholarflow.presentation.main.view.DashboardSidebar;
import com.scholarflow.presentation.main.view.PaperDetailsFrame;
import com.scholarflow.presentation.main.view.PaperListPanel;
import com.scholarflow.presentation.main.view.PaperSubmissionPanel;

public final class DashboardController {
    private final User user;
    private final Translator translator;
    private final PaperService paperService;
    private final FieldService fieldService;
    private final InteractionService interactionService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final AuditService auditService;
    private final XmlService xmlService;
    private final DashboardSidebar sidebar;
    private final JPanel contentContainer;
    private final JFrame frame;

    public DashboardController(
        final User user,
        final Translator translator,
        final PaperService paperService,
        final FieldService fieldService,
        final InteractionService interactionService,
        final UserService userService,
        final ReviewService reviewService,
        final AuditService auditService,
        final XmlService xmlService,
        final DashboardSidebar sidebar,
        final JPanel contentContainer,
        final JFrame frame
    ) {
        this.user = Objects.requireNonNull(user);
        this.translator = Objects.requireNonNull(translator);
        this.paperService = Objects.requireNonNull(paperService);
        this.fieldService = Objects.requireNonNull(fieldService);
        this.interactionService = Objects.requireNonNull(interactionService);
        this.userService = Objects.requireNonNull(userService);
        this.reviewService = Objects.requireNonNull(reviewService);
        this.auditService = Objects.requireNonNull(auditService);
        this.xmlService = Objects.requireNonNull(xmlService);
        this.sidebar = Objects.requireNonNull(sidebar);
        this.contentContainer = Objects.requireNonNull(contentContainer);
        this.frame = Objects.requireNonNull(frame);
        
        this.init();
    }

    private void init() {
        this.sidebar.onDashboardClick(this::showWelcomeMessage);
        this.sidebar.onAllPapersClick(() -> this.replaceContent(new JLabel("All Papers List")));
        this.sidebar.onMyPapersClick(() -> this.replaceContent(new JLabel("My Submissions")));
        this.sidebar.onReviewTasksClick(() -> this.replaceContent(new JLabel("Review Tasks")));
        this.sidebar.onLogoutClick(this::handleLogout);
        this.sidebar.onAllPapersClick(this::handleShowAllPapers);
        // ONly for researches
        this.sidebar.onMyPapersClick(this::handleShowMyPapers);
        this.sidebar.onNewPaperClick(this::handleOpenSubmissionForm);

        this.sidebar.onReviewAssignmentsClick(() -> {
            this.replaceContent(new JLabel("Admin Review Management"));
        });
    }

    private void handleShowAllPapers() {
        this.replaceContent(new JLabel(translator.translate("status.loading"), SwingConstants.CENTER));

        new SwingWorker<TableLoadResult, Void>() {
            @Override
            protected TableLoadResult doInBackground() {
                // Load articles
                List<Paper> papers = user.hasAdminPrivileges()
                    ? paperService.findAllForAdmin(user)
                    : paperService.findPublishedPapers();

                Map<UUID, String> fieldMap = fieldService.getAllFields().stream()
                    .collect(Collectors.toMap(
                        f -> f.id().orElseThrow(), // Key
                        f -> f.localizedName(translator.currentLanguage())
                    ));

                return new TableLoadResult(papers, fieldMap);
            }

            @Override
            protected void done() {
                updateUIWithData(this);
            }
        }.execute();
    }

    private void handleShowMyPapers() {
        this.replaceContent(new JLabel(translator.translate("status.loading"), SwingConstants.CENTER));

        new SwingWorker<TableLoadResult, Void>() {
            @Override
            protected TableLoadResult doInBackground() {
                // Load articles
                List<Paper> papers = paperService.findByAuthor(user.id().orElseThrow());

                Map<UUID, String> fieldMap = fieldService.getAllFields().stream()
                    .collect(Collectors.toMap(
                        f -> f.id().orElseThrow(), // Key
                        f -> f.localizedName(translator.currentLanguage())
                    ));

                return new TableLoadResult(papers, fieldMap);
            }

            @Override
            protected void done() {
                updateUIWithData(this);
            }
        }.execute();
    }

    private void updateUIWithData(SwingWorker<TableLoadResult, Void> worker) {
        try {
            TableLoadResult result = worker.get();

            PaperTableModel model = new PaperTableModel(
                result.papers(),
                translator,
                result.fieldMap()
            );

            PaperListPanel listPanel = new PaperListPanel(model, translator);

            listPanel.onPaperSelected(paper -> {
                new PaperDetailsFrame(
                    paper, 
                    user, 
                    userService,
                    reviewService,
                    translator, 
                    interactionService
                ).open();
            });

            this.replaceContent(listPanel);
        } catch (Exception e) {
            this.replaceContent(new JLabel("Error: " + e.getMessage()));
        }
    }

    public void showWelcomeMessage() {
        final JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(Color.WHITE);

        final String text = String.format(translator.translate("welcome.message"), user.fullName());
        final JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI Light", Font.PLAIN, 26));
        label.setForeground(new Color(127, 140, 141));

        welcomePanel.add(label);
        this.replaceContent(welcomePanel);
    }

    private void replaceContent(final JComponent newContent) {
        contentContainer.removeAll();
        contentContainer.add(newContent, BorderLayout.CENTER);
        contentContainer.revalidate();
        contentContainer.repaint();
    }

    private void handleLogout() {
        final int choice = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to log out?",
            "Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            frame.dispose();
            System.out.println("User logged out.");
        }
    }

    private void handleOpenSubmissionForm() {
        PaperSubmissionPanel submissionPanel = new PaperSubmissionPanel(translator);

        new PaperSubmissionController(
            user, paperService, fieldService, translator,
            submissionPanel, this::handleShowMyPapers
        );

        this.replaceContent(submissionPanel);
    }

    private record TableLoadResult(
        List<Paper> papers,
        Map<UUID, String> fieldMap
    ) {}
}
