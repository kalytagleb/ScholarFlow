package com.scholarflow.business.model.state;

import java.util.Objects;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.enums.PaperStatus;

public final class UnderReviewState implements PaperState {
    private final Paper paper;
    private final PaperState except;

    public UnderReviewState(final Paper paper) {
        this.paper = Objects.requireNonNull(paper);
        this.except = new ExceptState(PaperStatus.UNDER_REVIEW);
    }

    @Override
    public PaperStatus status() {
        return PaperStatus.UNDER_REVIEW;
    }

    @Override
    public Paper submit() {
        return this.except.submit();
    }

    @Override
    public Paper startReview() {
        return this.except.startReview();
    }

    @Override
    public Paper accept() {
        return this.paper.withStatus(PaperStatus.ACCEPTED);
    }

    @Override
    public Paper reject() {
        return this.paper.withStatus(PaperStatus.REJECTED);
    }

    @Override
    public Paper requestRevision(final boolean major) {
        final PaperStatus nextStatus = major ? PaperStatus.MAJOR_REVISION : PaperStatus.MINOR_REVISION;

        return this.paper.withStatus(nextStatus);
    }

    @Override
    public Paper resubmit() {
        return this.except.resubmit();
    }
}
