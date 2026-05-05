package com.scholarflow.presentation.main.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.User;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.main.view.DashboardSidebar;
import com.scholarflow.presentation.main.view.PaperListPanel;

public final class DashboardController {
    private final User user;
    private final Translator translator;
    private final PaperService paperService;
    private final DashboardSidebar sidebar;
    private final JPanel contentContainer;
    private final JFrame frame;

    public DashboardController(
        final User user,
        final Translator translator,
        final PaperService paperService,
        final DashboardSidebar sidebar,
        final JPanel contentContainer,
        final JFrame frame
    ) {
        this.user = Objects.requireNonNull(user);
        this.translator = Objects.requireNonNull(translator);
        this.paperService = Objects.requireNonNull(paperService);
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
    }

    private void handleShowAllPapers() {
        this.replaceContent(new JLabel(translator.translate("status.loading"), SwingConstants.CENTER));

        new SwingWorker<List<Paper>, Void>() {
            @Override
            protected List<Paper> doInBackground() {
                if (user.hasAdminPrivileges()) {
                    return paperService.findAllForAdmin(user);
                }

                return paperService.findPublishedPapers();
            }

            @Override
            protected void done() {
                updateUIWithData(this);
            }
        }.execute();
    }

    private void handleShowMyPapers() {
        this.replaceContent(new JLabel(translator.translate("status.loading")));

        new SwingWorker<List<Paper>, Void>() {
            @Override
            protected List<Paper> doInBackground() {
                return paperService.findByAuthor(user.id().orElseThrow());
            }

            @Override
            protected void done() {
                updateUIWithData(this);
            }
        }.execute();
    }

    private void updateUIWithData(SwingWorker<List<Paper>, Void> worker) {
        try {
            List<Paper> data = worker.get();

            this.replaceContent(new PaperListPanel(data, translator));
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
}
