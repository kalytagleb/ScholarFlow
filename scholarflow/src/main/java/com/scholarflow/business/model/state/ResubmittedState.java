package com.scholarflow.business.model.state;

import java.util.Objects;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.PaperStatus;

public final class ResubmittedState implements PaperState {
    private final Paper paper;
    private final PaperState except;

    public ResubmittedState(final Paper paper) {
        this.paper = Objects.requireNonNull(paper);
        this.except = new ExceptState(PaperStatus.RESUBMITTED);
    }

    @Override
    public PaperStatus status() {
        return PaperStatus.RESUBMITTED;
    }

    @Override
    public Paper submit() {
        return this.except.submit();
    }

    @Override
    public Paper startReview() {
        return this.paper.withStatus(PaperStatus.UNDER_REVIEW);
    }

    // Admin can reject paper from the beginning
    @Override
    public Paper reject() {
        return this.except.reject();
    }

    // In this state - accepting is not possible
    @Override
    public Paper accept() {
        return this.except.accept();
    }

    @Override
    public Paper requestRevision(boolean major) {
        return this.except.requestRevision(major);
    }

    @Override
    public Paper resubmit() {
        return this.except.resubmit();
    }
}
