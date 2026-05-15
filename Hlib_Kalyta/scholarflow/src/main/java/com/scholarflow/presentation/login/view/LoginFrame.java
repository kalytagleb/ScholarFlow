package com.scholarflow.presentation.login.view;

import javax.swing.JFrame;

import com.scholarflow.business.service.AuditService;
import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.InteractionService;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.ReviewService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.business.service.XmlService;
import com.scholarflow.presentation.login.controller.LoginController;

public final class LoginFrame {
    private final JFrame frame;

    public LoginFrame(
        final UserService userService,
        final ReviewService reviewService,
        final FieldService fieldService,
        final PaperService paperService,
        final InteractionService interactionService,
        final Translator translator,
        final AuditService auditService,
        final XmlService xmlService
    ) {
        this.frame = new JFrame("Scholarflow - " + translator.translate("login.title"));

        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(400, 500);
        this.frame.setLocationRelativeTo(null);

        final LoginPanel panel = new LoginPanel(translator);
        new LoginController(
            userService,
            reviewService,
            fieldService,
            panel,
            frame,
            translator,
            paperService,
            interactionService,
            auditService,
            xmlService
        );

        this.frame.add(panel);
    }

    public void open() {
        this.frame.setVisible(true);
    }
}
