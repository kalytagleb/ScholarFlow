package com.scholarflow.business.model.state;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.PaperStatus;

/**
 * Interface, which describes paper behavior in some state.
 * Every method returns new example of Paper 
 */
public interface PaperState {
    PaperStatus status();
    Paper submit();
    Paper startReview();
    Paper accept();
    Paper reject();
    Paper requestRevision(boolean major);
    Paper resubmit();
}
