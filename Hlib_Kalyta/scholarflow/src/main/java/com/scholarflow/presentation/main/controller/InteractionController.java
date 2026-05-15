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
import com.scholarflow.presentation.main.view.PaperDetailsFrame;

public final class InteractionController {
    private final InteractionService service;
    private final Translator translator;
    private final User currentUser;
    private final UUID paperId;

    private final PaperCommentsPanel commentsView;
    private final PaperDetailsFrame detailsFrame;

    public InteractionController(
        final InteractionService service,
        final Translator translator,
        final User currentUser,
        final UUID paperId,
        final PaperCommentsPanel commentsView,
        final PaperDetailsFrame detailsFrame
    ) {
        this.service = Objects.requireNonNull(service);
        this.translator = Objects.requireNonNull(translator);
        this.currentUser = Objects.requireNonNull(currentUser);
        this.paperId = Objects.requireNonNull(paperId);
        this.commentsView = Objects.requireNonNull(commentsView);
        this.detailsFrame = Objects.requireNonNull(detailsFrame);

        this.init();
    }

    private void init() {
        this.commentsView.onPostComment(this::handlePostComment);
        this.detailsFrame.onLikeClick(this::handleLikeToggle);
        this.refreshAll();
    }

    private void refreshAll() {
        this.refreshComments();
        this.refreshLikes();
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
                    commentsView.displayComments(get());
                } catch (Exception e) {
                    System.err.println("Failed to load comments: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void refreshLikes() {
        new SwingWorker<LikeData, Void>() {
            @Override
            protected LikeData doInBackground() {
                long count = service.getLikeCount(paperId);
                boolean liked = service.hasUserLiked(paperId, currentUser.id().orElseThrow());

                return new LikeData(count, liked);
            }

            @Override
            protected void done() {
                try {
                    LikeData data = get();
                    detailsFrame.updateLikeUI(data.count(), data.isLiked());
                } catch (Exception e) {
                    System.err.println("Failed to load comments: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void handleLikeToggle() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                service.toggleLike(paperId, currentUser);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    refreshLikes();
                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    JOptionPane.showMessageDialog(null, msg);
                }
            }
        }.execute();
    }

    // Logic to send new comment
    private void handlePostComment() {
        final String text = commentsView.commentText();

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
                    commentsView.clearInput();
                    refreshComments();
                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    JOptionPane.showMessageDialog(null, msg, "Comment Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private record LikeData(long count, boolean isLiked) {}
}
