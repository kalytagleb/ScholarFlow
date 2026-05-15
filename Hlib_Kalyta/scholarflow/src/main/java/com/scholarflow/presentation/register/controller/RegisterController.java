package com.scholarflow.presentation.register.controller;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.scholarflow.business.model.Field;
import com.scholarflow.business.model.User;
import com.scholarflow.business.service.AuditService;
import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.InteractionService;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.ReviewService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.business.service.XmlService;
import com.scholarflow.presentation.main.view.DashboardFrame;
import com.scholarflow.presentation.register.view.RegisterPanel;

public final class RegisterController {
    private final UserService userService;
    private final ReviewService reviewService;
    private final FieldService fieldService;
    private final Translator translator;
    private final RegisterPanel view;
    private final PaperService paperService;
    private final InteractionService interactionService;
    private final AuditService auditService;
    private final XmlService xmlService;

    private final JFrame registerFrame;
    private final JFrame loginFrame;

    private List<Field> fields;

    public RegisterController(
        final UserService userService,
        final ReviewService reviewService,
        final FieldService fieldService,
        final PaperService paperService,
        final InteractionService interactionService,
        final Translator translator,
        final RegisterPanel view,
        final JFrame registerFrame,
        final JFrame loginFrame,
        final AuditService auditService,
        final XmlService xmlService
    ) {
        this.userService        = Objects.requireNonNull(userService);
        this.reviewService      = Objects.requireNonNull(reviewService);
        this.fieldService       = Objects.requireNonNull(fieldService);
        this.paperService       = Objects.requireNonNull(paperService);
        this.interactionService = Objects.requireNonNull(interactionService);
        this.translator         = Objects.requireNonNull(translator);
        this.view               = Objects.requireNonNull(view);
        this.registerFrame      = Objects.requireNonNull(registerFrame);
        this.loginFrame         = Objects.requireNonNull(loginFrame);
        this.auditService       = Objects.requireNonNull(auditService);
        this.xmlService         = Objects.requireNonNull(xmlService);
        this.init();
    }

    private void init() {
        this.view.onRegister(this::handleRegistration);
        this.view.onBack(this::handleBack);
        this.loadInitialData();
    }

    private void handleBack() {
        this.registerFrame.dispose();
    }

    private void loadInitialData() {
        new SwingWorker<List<Field>, Void>() {
            @Override
            protected List<Field> doInBackground() {
                return fieldService.getAllFields();
            }

            @Override
            protected void done() {
                try {
                    fields = get();
                    final List<String> names = fields.stream()
                        .map(Field::nameEn)
                        .collect(Collectors.toList());
                    view.setFieldNames(names);
                } catch (Exception e) {
                    view.displayError(translator.translate("error.load_failed"));
                }
            }
        }.execute();
    }

    private void handleRegistration() {
        final String selectedName = view.selectedFieldName();

        UUID fieldId = null;
        if (fields != null) {
            for (Field f : fields) {
                if (f.nameEn().equals(selectedName)) {
                    fieldId = f.id().orElse(null);
                    break;
                }
            }
        }

        final UUID finalFieldId = fieldId;

        view.setLock(true);
        view.displayError(translator.translate("status.registering"));

        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userService.registerUser(
                    view.username(),
                    view.password(),
                    view.email(),
                    view.fullName(),
                    view.role(),
                    finalFieldId
                );
            }

            @Override
            protected void done() {
                try {
                    final User newUser = get();

                    registerFrame.dispose();
                    loginFrame.dispose();

                    new DashboardFrame(
                        newUser,
                        translator,
                        paperService,
                        fieldService,
                        interactionService,
                        userService,
                        reviewService,
                        auditService,
                        xmlService
                    ).open();

                    JOptionPane.showMessageDialog(
                        null,
                        translator.translate("register.success")
                    );
                } catch (Exception e) {
                    view.setLock(false);
                    final String msg = e.getCause() != null
                        ? e.getCause().getMessage()
                        : e.getMessage();
                    view.displayError(msg != null ? msg : "Registration failed");
                }
            }
        }.execute();
    }
}
