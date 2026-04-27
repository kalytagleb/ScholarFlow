package com.scholarflow.business.model.state;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.PaperStatus;

/*
    Here all methods (by default) throws exception.
*/

public final class ExceptState implements PaperState {
    private final PaperStatus status;

    public ExceptState(final PaperStatus status) {
        this.status = status;
    }

    @Override
    public PaperStatus status() {
        return this.status;
    }

    @Override
    public Paper submit() {
        throw new IllegalStateException("Action SUBMIT is not allowed in state " + status);
    }

    @Override
    public Paper startReview() {
        throw new IllegalStateException("Action START_REVIEW is not allowed in state " + status);
    }

    @Override
    public Paper accept() {
        throw new IllegalStateException("Action ACCEPT is not allowed in state " + status);
    }

    @Override
    public Paper reject() {
        throw new IllegalStateException("Action REJECT is not allowed in state " + status);
    }

    @Override
    public Paper requestRevision(boolean major) {
        throw new IllegalStateException("Action REVISION is not allowed in state " + status);
    }

    @Override
    public Paper resubmit() {
        throw new IllegalStateException("Action RESUBMIT is not allowed in state " + status);
    }
}
