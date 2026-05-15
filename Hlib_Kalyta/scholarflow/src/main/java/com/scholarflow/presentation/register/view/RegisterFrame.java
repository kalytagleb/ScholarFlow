package com.scholarflow.presentation.register.view;

import javax.swing.JFrame;

import com.scholarflow.business.service.AuditService;
import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.InteractionService;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.ReviewService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.business.service.XmlService;
import com.scholarflow.presentation.register.controller.RegisterController;

public final class RegisterFrame {
    private final JFrame frame;

    public RegisterFrame(
        final UserService userService,
        final ReviewService reviewService,
        final FieldService fieldService,
        final PaperService paperService,
        final Translator translator,
        final JFrame loginFrame,
        final InteractionService interactionService,
        final AuditService auditService,
        final XmlService xmlService
    ) {
        this.frame = new JFrame("ScholarFlow - Create Account");

        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setSize(450, 650);
        this.frame.setLocationRelativeTo(null);

        final RegisterPanel panel = new RegisterPanel(translator);
        new RegisterController(
            userService,
            reviewService,
            fieldService,
            paperService,
            interactionService,
            translator,
            panel,
            this.frame,
            loginFrame,
            auditService,
            xmlService
        );

        this.frame.add(panel);
    }

    public void open() {
        this.frame.setVisible(true);
    }
}
