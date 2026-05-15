package com.scholarflow.presentation.main.controller;

import java.util.Objects;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.scholarflow.business.model.User;
import com.scholarflow.business.model.Paper;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.main.view.PaperDetailsFrame;

public final class PaperDetailsController {
    private final PaperService paperService;
    private final Translator translator;
    private final Paper paper;
    private final User user;
    private final PaperDetailsFrame view;

    public PaperDetailsController(
        final PaperService paperService,
        final Translator translator,
        final Paper paper,
        final User user,
        final PaperDetailsFrame view
    ) {
        this.paperService = Objects.requireNonNull(paperService);
        this.translator = Objects.requireNonNull(translator);
        this.paper = Objects.requireNonNull(paper);
        this.user = Objects.requireNonNull(user);
        this.view = Objects.requireNonNull(view);

        this.init();
    }

    private void init() {
        this.view.onSubmitClick(this::handleSubmission);
    }

    private void handleSubmission() {
        int confirm = JOptionPane.showConfirmDialog(
            null, 
            translator.translate("details.confirm_submit"),
            "Confirm",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                paperService.submitPaper(paper.id().orElseThrow(), user);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(null, translator.translate("details.submit_success"));
                    view.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
            }
        }.execute();
    }
}
