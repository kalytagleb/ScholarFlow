package com.scholarflow.presentation.main.controller;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.scholarflow.business.model.PaperComment;
import com.scholarflow.business.model.User;
import com.scholarflow.business.service.InteractionService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.main.view.PaperCommentsPanel;

public final class InteractionController {
    private final InteractionService service;
    private final Translator translator;
    private final User currentUser;
    private final UUID paperId;
    private final PaperCommentsPanel view;

    public InteractionController(
        final InteractionService service,
        final Translator translator,
        final User currentUser,
        final UUID paperId,
        final PaperCommentsPanel view
    ) {
        this.service = Objects.requireNonNull(service);
        this.translator = Objects.requireNonNull(translator);
        this.currentUser = Objects.requireNonNull(currentUser);
        this.paperId = Objects.requireNonNull(paperId);
        this.view = Objects.requireNonNull(view);

        this.init();
    }

    private void init() {
        this.view.onPostComment(this::handlePostComment);
        this.refreshComments();
    }

    private void refreshComments() {
        new SwingWorker<List<PaperComment>, Void>() {
            @Override
            protected List<PaperComment> doInBackground() {
                return service.getComments(paperId);
            }

            @Override
            protected void done() {
                try {
                    view.displayComments(get());
                } catch (Exception e) {
                    System.err.println("Failed to load comments: " + e.getMessage());
                }
            }
        }.execute();
    }

    // Logic to send new comment
    private void handlePostComment() {
        final String text = view.commentText();

        if (text.trim().isEmpty()) {
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                service.postComment(paperId, currentUser, text);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    view.clearInput();
                    refreshComments();
                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    JOptionPane.showMessageDialog(null, msg, "Comment Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
