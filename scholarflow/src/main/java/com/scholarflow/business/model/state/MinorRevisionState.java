package com.scholarflow.business.model.state;

import java.util.Objects;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.PaperStatus;

public final class MinorRevisionState implements PaperState {
    private final Paper paper;
    private final PaperState except;

    public MinorRevisionState(final Paper paper) {
        this.paper = Objects.requireNonNull(paper);
        this.except = new ExceptState(PaperStatus.MINOR_REVISION);
    }

    @Override
    public PaperStatus status() {
        return PaperStatus.MINOR_REVISION;
    }

    @Override
    public Paper resubmit() {
        return this.paper.withStatus(PaperStatus.RESUBMITTED);
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
        return this.except.accept();
    }

    @Override
    public Paper reject() {
        return this.except.reject();
    }

    @Override
    public Paper requestRevision(final boolean major) {
        return this.except.requestRevision(major);
    }
}
