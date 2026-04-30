package com.scholarflow.presentation.login.view;

import javax.swing.JFrame;

import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.presentation.login.controller.LoginController;

public final class LoginFrame {
    private final JFrame frame;
    private final Translator translator;

    public LoginFrame(final UserService userService, final FieldService fieldService, final Translator translator) {
        this.translator = translator;

        this.frame = new JFrame("Scholarflow - Sign In");

        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(400, 500);

        this.frame.setLocationRelativeTo(null);

        final LoginPanel panel = new LoginPanel();
        new LoginController(userService, fieldService, panel, frame, translator);

        this.frame.add(panel);
    }

    public void open() {
        this.frame.setVisible(true);
    }
}
