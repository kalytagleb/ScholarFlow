package com.scholarflow.business.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.PaperComment;
import com.scholarflow.business.model.PaperLike;
import com.scholarflow.business.model.User;
import com.scholarflow.business.model.enums.PaperStatus;
import com.scholarflow.data.repository.PaperCommentRepository;
import com.scholarflow.data.repository.PaperLikeRepository;
import com.scholarflow.data.repository.PaperRepository;

public final class InteractionService {
    private final PaperCommentRepository comments;
    private final PaperLikeRepository likes;
    private final PaperRepository papers;

    public InteractionService(
        final PaperCommentRepository comments,
        final PaperLikeRepository likes,
        final PaperRepository papers
    ) {
        this.comments = Objects.requireNonNull(comments);
        this.likes = Objects.requireNonNull(likes);
        this.papers = Objects.requireNonNull(papers);
    }

    public PaperComment postComment(final UUID paperId, final User user, final String text) {
        if (!user.canInteract()) {
            throw new IllegalStateException("User account is inactive");
        }

        final Paper paper = this.papers.findById(paperId)
            .orElseThrow(() -> new IllegalArgumentException("Paper not found."));

        if (paper.status() != PaperStatus.ACCEPTED) {
            throw new IllegalStateException("Comments are only allowed for accepted papers.");
        }

        final UUID userId = user.id().orElseThrow();

        final PaperComment comment = new PaperComment(paperId, userId, text);
        this.comments.save(comment);

        return comment;
    }

    public void toggleLike(final UUID paperId, final User user) {
        if (!user.canInteract()) {
            throw new IllegalStateException("User account is inactive");
        }

        final Paper paper = this.papers.findById(paperId)
            .orElseThrow(() -> new IllegalArgumentException("Paper not found."));

        if (paper.status() != PaperStatus.ACCEPTED) {
            throw new IllegalStateException("Likes are only allowed for accepted papers.");
        }

        final UUID userId = user.id().orElseThrow();

        // If paper has like from that user - remove it
        if (this.likes.exists(paperId, userId)) {
            this.likes.delete(paperId, userId);
        } else {
            this.likes.save(new PaperLike(paperId, userId));
        }
    }

    public List<PaperComment> getComments(final UUID paperId) {
        return this.comments.findByPaper(paperId);
    }

    public long getLikeCount(final UUID paperId) {
        return this.likes.countByPaper(paperId);
    }
}
