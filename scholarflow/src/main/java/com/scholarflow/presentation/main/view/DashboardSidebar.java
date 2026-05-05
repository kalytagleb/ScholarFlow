package com.scholarflow.presentation.main.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.scholarflow.business.model.User;
import com.scholarflow.business.service.Translator;

public final class DashboardSidebar extends JPanel {
    private final User user;
    private final Translator translator;

    private final JButton dashboardBtn;
    private final JButton allPapersBtn;
    private final JButton myPapersBtn;
    private final JButton reviewTasksBtn;
    private final JButton logoutBtn;
    private final JButton newPaperBtn;

    public DashboardSidebar(final User user, final Translator translator) {
        this.user = Objects.requireNonNull(user);
        this.translator = Objects.requireNonNull(translator);

        this.dashboardBtn = createMenuButton("menu.dashboard");
        this.allPapersBtn = createMenuButton("menu.all_papers");
        this.myPapersBtn = createMenuButton("menu.my_submissions");
        this.reviewTasksBtn = createMenuButton("menu.review_tasks");
        this.logoutBtn = createMenuButton("menu.logout");
        this.newPaperBtn = createMenuButton("menu.new_paper");

        this.setupLayout();
    }

    private void setupLayout() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(260, 0));
        this.setBackground(new Color(44, 62, 80));
        this.setBorder(new EmptyBorder(30, 15, 30, 15));

        this.addProfileHeader();
        this.add(Box.createRigidArea(new Dimension(0, 40)));
        
        this.add(dashboardBtn);
        this.add(Box.createRigidArea(new Dimension(0, 5)));
        this.add(allPapersBtn);
        this.add(Box.createRigidArea(new Dimension(0, 5)));

        if (user.canSubmitPapers()) {
            this.add(myPapersBtn);
            this.add(Box.createRigidArea(new Dimension(0, 5)));
            this.add(newPaperBtn);
            this.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        if (user.canReview()) {
            this.add(reviewTasksBtn);
            this.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        this.add(Box.createVerticalGlue());
        this.add(logoutBtn);
    }

    private void addProfileHeader() {
        JLabel nameLabel = new JLabel(user.fullName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(user.role());
        roleLabel.setForeground(new Color(189, 195, 199));
        roleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.add(nameLabel);
        this.add(roleLabel);
    }

    private JButton createMenuButton(String key) {
        JButton btn = new JButton(translator.translate(key));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setBackground(new Color(44, 62, 80));
        btn.setForeground(new Color(189, 195, 199));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // For controllers
    public void onDashboardClick(Runnable action) { dashboardBtn.addActionListener(e -> action.run()); }
    public void onAllPapersClick(Runnable action) { allPapersBtn.addActionListener(e -> action.run()); }
    public void onMyPapersClick(Runnable action) { myPapersBtn.addActionListener(e -> action.run()); }
    public void onReviewTasksClick(Runnable action) { reviewTasksBtn.addActionListener(e -> action.run()); }
    public void onLogoutClick(Runnable action) { logoutBtn.addActionListener(e -> action.run()); }

    public void onNewPaperClick(Runnable action) {
        newPaperBtn.addActionListener(e -> action.run());
    }
}
