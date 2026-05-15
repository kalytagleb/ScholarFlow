package com.scholarflow.presentation.main.controller;

import java.util.Objects;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.scholarflow.business.model.enums.ReviewDecision;
import com.scholarflow.business.service.ReviewService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.main.view.ReviewSubmitPanel;

public final class ReviewSubmitController {
    private final ReviewService reviewService;
    private final Translator translator;
    private final UUID assignmentId;
    private final ReviewSubmitPanel view;

    private final Runnable onSuccess;

    public ReviewSubmitController(
        final ReviewService reviewService,
        final Translator translator,
        final UUID assignmentId,
        final ReviewSubmitPanel view,
        final Runnable onSuccess
    ) {
        this.reviewService = Objects.requireNonNull(reviewService);
        this.translator = Objects.requireNonNull(translator);
        this.assignmentId = Objects.requireNonNull(assignmentId);
        this.view = Objects.requireNonNull(view);
        this.onSuccess = Objects.requireNonNull(onSuccess);

        this.init();
    }

    private void init() {
        this.view.onSubmit(this::handleSubmit);
    }

    private void handleSubmit() {
        final ReviewDecision decision = view.selectedDecision();
        final String comments = view.comments();
        final String privateNotes = view.privateNotes();

        if (comments.trim().length() < 20) {
            view.displayError(translator.translate("error.review_too_short"));
            return;
        }

        view.setLock(true);
        view.displayError(translator.translate("status.submitting_review"));

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                reviewService.submitReview(assignmentId, decision, comments, privateNotes);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); 
                    
                    JOptionPane.showMessageDialog(null, translator.translate("review.submit_success"));
                    
                    onSuccess.run();

                } catch (Exception e) {
                    view.setLock(false);
                    final String msg = e.getCause() != null
                        ? e.getCause().getMessage()
                        : e.getMessage();
                    view.displayError(msg != null ? msg : "Unknown error");
                }
            }
        }.execute();
    }
}
