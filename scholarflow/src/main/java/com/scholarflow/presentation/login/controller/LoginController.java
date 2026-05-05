package com.scholarflow.presentation.login.controller;

import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import com.scholarflow.business.model.User;
import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.InteractionService;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.ReviewService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
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

    public LoginController(
        UserService userService, 
        ReviewService reviewService,
        FieldService fieldService, 
        LoginPanel view, 
        JFrame frame, 
        Translator translator, 
        PaperService paperService,
        InteractionService interactionService
    ) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.fieldService = fieldService;
        this.view = view;
        this.frame = frame;
        this.translator = translator;
        this.paperService = paperService;
        this.interactionService = interactionService;
        this.init();
    }

    private void init() {
        this.view.onLogin(this::handleLogin);
        this.view.onRegisterNavigate(this::handleOpenRegister);
        this.view.onLanguageChange(this::handleLanguageChange);
    }

    private void handleLanguageChange() {
        Translator newTranslator = new Translator(view.selectedLanguage());
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

        // We create separate thread for DB and return result into UI thread
        new SwingWorker<Optional<User>, Void>() {
            @Override
            protected Optional<User> doInBackground() {
                return userService.authenticate(name, pass);
            }

            @Override
            protected void done() {
                try {
                    Optional<User> user = get();
                    if (user.isPresent()) {
                        frame.dispose();
                        // JOptionPane.showMessageDialog(null, "Welcome, " + user.get().fullName());

                        User loggedUser = user.get();
                        new DashboardFrame(
                            loggedUser, 
                            translator, 
                            paperService, 
                            fieldService, 
                            interactionService,
                            userService,
                            reviewService
                        ).open();

                        System.out.println("User " + loggedUser.username() + " opened dashboard.");
                    } else {
                        view.setLock(false);
                        view.displayError("Invalid username or password");
                    }
                } catch (Exception e) {
                    view.setLock(false);
                    view.displayError("Connection error.");
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void handleOpenRegister() {
        new RegisterFrame(userService, reviewService, fieldService, paperService, translator, this.frame, interactionService).open();
    }
}
