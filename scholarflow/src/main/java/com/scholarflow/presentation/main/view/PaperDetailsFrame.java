package com.scholarflow.presentation.main.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.User;
import com.scholarflow.business.model.enums.PaperStatus;
import com.scholarflow.business.service.InteractionService;
import com.scholarflow.business.service.ReviewService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.presentation.common.Card;
import com.scholarflow.presentation.common.StatusBadge;
import com.scholarflow.presentation.main.controller.InteractionController;
import com.scholarflow.presentation.main.controller.ReviewAssignmentController;
import com.scholarflow.presentation.main.controller.ReviewSubmitController;

public final class PaperDetailsFrame {
    private final JFrame frame;
    private final Paper paper;
    private final UserService userService;
    private final ReviewService reviewService;
    private final Translator translator;
    private final User currentUser;
    private final InteractionService interactionService;
    private JButton likeBtn;
    private JLabel likeCountLabel;
    private JButton submitActionBtn;

    public PaperDetailsFrame(
        final Paper paper, 
        final User currentUser,
        final UserService userService,
        final ReviewService reviewService,
        final Translator translator,
        final InteractionService interactionService
    ) {
        this.paper = Objects.requireNonNull(paper);
        this.currentUser = Objects.requireNonNull(currentUser);
        this.userService = Objects.requireNonNull(userService);
        this.reviewService = Objects.requireNonNull(reviewService);
        this.translator = Objects.requireNonNull(translator);
        this.interactionService = Objects.requireNonNull(interactionService);
        this.frame = new JFrame(translator.translate("details.title"));
        this.setupUI();
    }

    private void setupUI() {
        frame.setSize(800, 750);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(245, 246, 250));

        root.add(createHeaderSection(), BorderLayout.NORTH);

        JPanel contentArea = createContentSection();
        JScrollPane mainScroll = new JScrollPane(contentArea);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        root.add(mainScroll, BorderLayout.CENTER);
        root.add(createFooterSection(), BorderLayout.SOUTH);

        frame.add(root);
    }

    private JPanel createHeaderSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 221, 225)),
            new EmptyBorder(20, 25, 20, 25)
        ));

        JLabel title = new JLabel("<html><body style='width: 450px'>" + paper.title() + "</body></html>");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(44, 62, 80));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actions.setBackground(Color.WHITE);

        likeBtn = new JButton("❤️");
        likeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        likeBtn.setContentAreaFilled(false);
        likeBtn.setBorderPainted(false);
        likeBtn.setFocusPainted(false);
        likeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        likeCountLabel = new JLabel("0");
        likeCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        likeCountLabel.setForeground(new Color(127, 140, 141));

        StatusBadge statusBadge = new StatusBadge(
            paper.status(),
            translator.translate("status." + paper.status().name().toLowerCase())
        );

        actions.add(likeCountLabel);
        actions.add(likeBtn);
        actions.add(statusBadge);

        panel.add(title, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.EAST);

        return panel;
    }

    private JPanel createContentSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 246, 250));
        panel.setBorder(new EmptyBorder(20, 20, 20, 25));

        Card abstractCard = new Card(25);
        abstractCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel absHeader = new JLabel(translator.translate("details.abstract"));
        absHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        absHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea absText = new JTextArea(paper.paperAbstract());
        absText.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        absText.setLineWrap(true);
        absText.setWrapStyleWord(true);
        absText.setEditable(false);
        absText.setOpaque(false);
        absText.setAlignmentX(Component.LEFT_ALIGNMENT);

        abstractCard.add(absHeader);
        abstractCard.add(Box.createRigidArea(new Dimension(0, 10)));
        abstractCard.add(absText);

        panel.add(abstractCard);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        PaperCommentsPanel commentsView = new PaperCommentsPanel(translator);
        commentsView.setAlignmentX(Component.LEFT_ALIGNMENT);

        new InteractionController(
            interactionService,
            translator,
            currentUser,
            paper.id().orElseThrow(),
            commentsView,
            this
        );

        panel.add(commentsView);

        // For reviewer
        if (currentUser.canReview()) {
            var myTask = reviewService.findActiveAssignment(
                paper.id().orElseThrow(),
                currentUser.id().orElseThrow()
            );

            if (myTask.isPresent()) {
                panel.add(Box.createRigidArea(new Dimension(0, 25)));
                ReviewSubmitPanel submitView = new ReviewSubmitPanel(translator);
                submitView.setAlignmentX(Component.LEFT_ALIGNMENT);

                new ReviewSubmitController(
                    reviewService, 
                    translator, 
                    myTask.get().id().get(), 
                    submitView, 
                    () -> {
                        submitView.setVisible(false);
                        JOptionPane.showMessageDialog(null, translator.translate("review.submit_success"));
                    }
                );

                panel.add(submitView);
            }
        }

        // For admin
        if (currentUser.hasAdminPrivileges() && paper.status() != PaperStatus.ACCEPTED) {
            panel.add(Box.createRigidArea(new Dimension(0, 25)));

            ReviewAssignmentPanel assignView = new ReviewAssignmentPanel(translator);
            assignView.setAlignmentX(Component.LEFT_ALIGNMENT);

            new ReviewAssignmentController(
                reviewService, 
                userService, 
                translator, 
                currentUser, 
                paper.id().get(), 
                assignView
            );

            panel.add(assignView);
        }

        return panel;
    }

    private JPanel createFooterSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setOpaque(false);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 221, 225)));

        JButton closeButton = new JButton(translator.translate("common.close"));
        closeButton.addActionListener(e -> frame.dispose());

        if (currentUser.id().get().equals(paper.submitterId()) && paper.status() == PaperStatus.DRAFT) {
            submitActionBtn = new JButton(translator.translate("details.submit_action"));
            submitActionBtn.setBackground(new Color(46, 204, 113));
            submitActionBtn.setForeground(Color.WHITE);
            panel.add(submitActionBtn);
        }

        panel.add(closeButton);
        return panel;
    }

    public void onLikeClick(Runnable action) {
        likeBtn.addActionListener(e -> action.run());
    }

    public void updateLikeUI(long count, boolean isLiked) {
        likeCountLabel.setText(String.valueOf(count));
    }

    public void onSubmitClick(Runnable action) {
        if (submitActionBtn != null) {
            submitActionBtn.addActionListener(e -> action.run());
        }
    }

    public void open() {
        frame.setVisible(true);
    }

    public void close() {
        frame.dispose();
    }
}
