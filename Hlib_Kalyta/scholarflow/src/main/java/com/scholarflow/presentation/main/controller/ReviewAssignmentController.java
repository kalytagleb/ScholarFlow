package com.scholarflow.presentation.main.controller;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.scholarflow.business.model.User;
import com.scholarflow.business.service.ReviewService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.presentation.main.view.ReviewAssignmentPanel;

public final class ReviewAssignmentController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final Translator translator;
    private final User admin;
    private final UUID paperId;
    private final ReviewAssignmentPanel view;

    private List<User> availableReviewers;

     public ReviewAssignmentController(
        final ReviewService reviewService,
        final UserService userService,
        final Translator translator,
        final User admin,
        final UUID paperId,
        final ReviewAssignmentPanel view
    ) {
        this.reviewService = Objects.requireNonNull(reviewService);
        this.userService = Objects.requireNonNull(userService);
        this.translator = Objects.requireNonNull(translator);
        this.admin = Objects.requireNonNull(admin);
        this.paperId = Objects.requireNonNull(paperId);
        this.view = Objects.requireNonNull(view);

        this.init();
    }

    private void init() {
        this.view.onAssign(this::handleAssign);
        this.loadReviewers();
    }

    private void loadReviewers() {
        new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() {
                return userService.findByRole("REVIEWER");
            }

            @Override
            protected void done() {
                try {
                    availableReviewers = get();

                    List<String> names = availableReviewers.stream()
                        .map(User::fullName)
                        .collect(Collectors.toList());
                    view.fillReviewers(names);
                } catch (Exception e) {
                    view.displayError("Failed to load reviewers");
                }
            }
        }.execute();
    }

    private void handleAssign() {
        final String reviewerName = view.selectedReviewerName();
        final String dateText = view.deadlineText();

        final LocalDate deadline;
        try {
            deadline = LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            view.displayError(translator.translate("error.invalid_date"));
            return;
        }

        final User selectedReviewer = availableReviewers.stream()
            .filter(u -> u.fullName().equals(reviewerName))
            .findFirst()
            .orElse(null);

        if (selectedReviewer == null || selectedReviewer.id().isEmpty()) {
            view.displayError("Please select a valid reviewer.");
            return;
        }

        view.setLock(true);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                reviewService.assign(
                    paperId, 
                    selectedReviewer.id().get(), 
                    admin, 
                    deadline
                );
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(null, translator.translate("review.assign_success"));
                    view.setLock(false);
                } catch (Exception e) {
                    view.setLock(false);
                    String msg = e.getCause().getMessage();
                    view.displayError(msg);
                }
            }
        }.execute();
    }
}
