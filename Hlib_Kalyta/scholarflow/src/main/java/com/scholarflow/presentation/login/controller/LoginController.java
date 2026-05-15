package com.scholarflow.presentation.login.controller;

import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import com.scholarflow.business.model.User;
import com.scholarflow.business.service.AuditService;
import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.InteractionService;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.ReviewService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.business.service.XmlService;
import com.scholarflow.presentation.login.view.LoginPanel;
import com.scholarflow.presentation.main.view.DashboardFrame;
import com.scholarflow.presentation.register.view.RegisterFrame;

public final class LoginController {
    private final UserService userService;
    private final ReviewService reviewService;
    private final LoginPanel view;
    private final JFrame frame;
    private final FieldService fieldService;
    private final InteractionService interactionService;
    private final Translator translator;
    private final PaperService paperService;
    private final AuditService auditService;
    private final XmlService xmlService;

    public LoginController(
        final UserService userService,
        final ReviewService reviewService,
        final FieldService fieldService,
        final LoginPanel view,
        final JFrame frame,
        final Translator translator,
        final PaperService paperService,
        final InteractionService interactionService,
        final AuditService auditService,
        final XmlService xmlService
    ) {
        this.userService        = userService;
        this.reviewService      = reviewService;
        this.fieldService       = fieldService;
        this.view               = view;
        this.frame              = frame;
        this.translator         = translator;
        this.paperService       = paperService;
        this.interactionService = interactionService;
        this.auditService       = auditService;
        this.xmlService         = xmlService;
        this.init();
    }

    private void init() {
        this.view.onLogin(this::handleLogin);
        this.view.onRegisterNavigate(this::handleOpenRegister);
        this.view.onLanguageChange(this::handleLanguageChange);
    }

    private void handleLanguageChange() {
        final Translator newTranslator = new Translator(view.selectedLanguage());
        view.updateTexts(newTranslator);
        frame.setTitle("Scholarflow - " + newTranslator.translate("login.title"));
    }

    private void handleLogin() {
        final String name = view.username();
        final String pass = view.password();

        if (name.isBlank() || pass.isBlank()) {
            view.displayError(translator.translate("error.empty_fields"));
            return;
        }

        view.setLock(true);

        new SwingWorker<Optional<User>, Void>() {
            @Override
            protected Optional<User> doInBackground() {
                return userService.authenticate(name, pass);
            }

            @Override
            protected void done() {
                try {
                    final Optional<User> user = get();
                    if (user.isPresent()) {
                        frame.dispose();
                        new DashboardFrame(
                            user.get(),
                            translator,
                            paperService,
                            fieldService,
                            interactionService,
                            userService,
                            reviewService,
                            auditService,
                            xmlService
                        ).open();
                    } else {
                        view.setLock(false);
                        view.displayError(translator.translate("error.invalid_credentials"));
                    }
                } catch (Exception e) {
                    view.setLock(false);
                    view.displayError(translator.translate("error.connection"));
                }
            }
        }.execute();
    }

    private void handleOpenRegister() {
        new RegisterFrame(
            userService,
            reviewService,
            fieldService,
            paperService,
            translator,
            this.frame,
            interactionService,
            auditService,
            xmlService
        ).open();
    }
}
