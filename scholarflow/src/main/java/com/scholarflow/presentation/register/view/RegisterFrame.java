package com.scholarflow.presentation.register.view;

import javax.swing.JFrame;

import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.UserService;
import com.scholarflow.presentation.register.controller.RegisterController;

public final class RegisterFrame {
    private final JFrame frame;

    public RegisterFrame(final UserService userService, final FieldService fieldService) {
        this.frame = new JFrame("ScholarFlow - Create Account");

        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setSize(450, 650);
        this.frame.setLocationRelativeTo(null);

        final RegisterPanel panel = new RegisterPanel();
        new RegisterController(userService, fieldService, panel, frame);

        this.frame.add(panel);
    }

    public void open() {
        this.frame.setVisible(true);
    }
}
