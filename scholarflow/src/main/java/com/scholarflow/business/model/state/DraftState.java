package com.scholarflow.business.model.state;

import java.util.Objects;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.PaperStatus;

public final class DraftState implements PaperState {
    private final Paper paper;
    private final PaperState except;

    public DraftState(final Paper paper) {
        this.paper = Objects.requireNonNull(paper);
        this.except = new ExceptState(PaperStatus.DRAFT);
    }

    @Override
    public PaperStatus status() {
        return PaperStatus.DRAFT;
    }

    // This action is allowed. We write logic for that here.
    @Override
    public Paper submit() {
        return this.paper.withStatus(PaperStatus.SUBMITTED);
    }

    @Override
    public Paper startReview() {
        return this.except.startReview();
    }

    @Override
    public Paper accept() {
        return this.except.accept();
    }

    @Override
    public Paper reject() {
        return this.except.reject();
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
