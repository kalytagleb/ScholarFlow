package com.scholarflow.presentation.main.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.scholarflow.business.model.User;
import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.InteractionService;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.ReviewService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.presentation.main.controller.DashboardController;

public final class DashboardFrame {
    private final JFrame frame;
    private final JPanel contentContainer;
    
    public DashboardFrame(
        final User user, 
        final Translator translator,
        final PaperService paperService, 
        final FieldService fieldService,
        final InteractionService interactionService,
        final UserService userService,
        final ReviewService reviewService
    ) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(translator);

        this.frame = new JFrame("Scholarflow - Academic Workflow");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.frame.setSize((int)(screenSize.width * 0.8), (int)(screenSize.height * 0.8));
        this.frame.setMinimumSize(new Dimension(1000, 700));
        this.frame.setLocationRelativeTo(null);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        DashboardSidebar sidebar = new DashboardSidebar(user, translator);

        this.contentContainer = new JPanel(new BorderLayout());
        this.contentContainer.setBackground(Color.WHITE);

        DashboardController controller = new DashboardController(
            user,
            translator, 
            paperService,
            fieldService,
            interactionService,
            userService,
            reviewService,
            sidebar, 
            contentContainer, 
            frame
        );

        root.add(sidebar, BorderLayout.WEST);
        root.add(contentContainer, BorderLayout.CENTER);

        this.frame.add(root);
        controller.showWelcomeMessage();
    }

    public void open() {
        this.frame.setVisible(true);
    }
}
