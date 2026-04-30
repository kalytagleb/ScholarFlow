package com.scholarflow.presentation.register.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.scholarflow.business.model.Field;
import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.UserService;
import com.scholarflow.presentation.register.view.RegisterPanel;

public final class RegisterController {
    private final UserService userService;
    private final FieldService fieldService;
    private final RegisterPanel view;
    private final JFrame frame;

    private List<Field> Fields;

    public RegisterController(
        UserService userService,
        FieldService fieldService,
        RegisterPanel view,
        JFrame frame
    ) {
        this.userService = userService;
        this.fieldService = fieldService;
        this.view = view;
        this.frame = frame;
        this.init();
    }

    private void init() {
        this.view.onRegister(this::handleRegistration);
        this.loadInitialData();
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
        view.displayError("Creating account, please wait...");
        
        new SwingWorker<Void,Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                userService.registerUser(
                    view.username(),
                    view.password(), 
                    view.email(), 
                    view.fullName(), 
                    view.role(), 
                    finalId
                );

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(frame, "Success!");
                    frame.dispose();
                } catch (Exception e) {
                    view.setLock(false);
                    view.displayError("Error: " + e.getCause().getMessage());
                }
            }
        }.execute();
    }
}
