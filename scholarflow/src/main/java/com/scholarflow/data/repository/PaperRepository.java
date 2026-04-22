package com.scholarflow.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.PaperStatus;

public interface PaperRepository {
    Optional<Paper> findById(UUID id);
    List<Paper> findAllBySubmitter(UUID submitterId);
    List<Paper> findAllByStatus(PaperStatus status);
    Paper save(Paper paper);
    void update(Paper paper);
}
