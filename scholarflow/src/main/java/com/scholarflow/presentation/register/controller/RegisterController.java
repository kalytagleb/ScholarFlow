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
import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.presentation.main.view.DashboardFrame;
import com.scholarflow.presentation.register.view.RegisterPanel;

public final class RegisterController {
    private final UserService userService;
    private final FieldService fieldService;
    private final Translator translator;
    private final RegisterPanel view;

    private final JFrame registerFrame;
    private final JFrame loginFrame;

    private List<Field> Fields;

    public RegisterController(
        UserService userService,
        FieldService fieldService,
        Translator translator,
        RegisterPanel view,
        JFrame registerFrame,
        JFrame loginFrame
    ) {
        this.userService = Objects.requireNonNull(userService);
        this.fieldService = Objects.requireNonNull(fieldService);
        this.translator = Objects.requireNonNull(translator);
        this.view = Objects.requireNonNull(view);
        this.registerFrame = Objects.requireNonNull(registerFrame);
        this.loginFrame = Objects.requireNonNull(loginFrame);
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
                    // Get all from doInBackground()
                    Fields = get();

                    List<String> names = Fields.stream()
                        .map(Field::nameEn)
                        .collect(Collectors.toList());

                    view.setFieldNames(names);
                } catch (Exception e) {
                    view.displayError("Failed to load fields.");
                }
            }
        }.execute();
    }

    private void handleRegistration() {
        final String selectedName = view.selectedFieldName();

        UUID fieldId = null;
        if (Fields != null) {
            for (Field f : Fields) {
                if (f.nameEn().equals(selectedName)) {
                    fieldId = f.id().orElse(null);
                    break;
                }
            }
        }

        final UUID finalId = fieldId;

        view.setLock(true);
        view.displayError(translator.translate("status.registering"));
        
        new SwingWorker<User,Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userService.registerUser(
                    view.username(),
                    view.password(), 
                    view.email(), 
                    view.fullName(), 
                    view.role(), 
                    finalId
                );
            }

            @Override
            protected void done() {
                try {
                    User newUser = get();
                    
                    registerFrame.dispose();
                    loginFrame.dispose();
                    
                    new DashboardFrame(newUser, translator).open();

                    JOptionPane.showMessageDialog(null, translator.translate("register.success_welcome") + " " + newUser.fullName());
                } catch (Exception e) {
                    view.setLock(false);
                    view.displayError("Error: " + e.getCause().getMessage());
                }
            }
        }.execute();
    }
}
