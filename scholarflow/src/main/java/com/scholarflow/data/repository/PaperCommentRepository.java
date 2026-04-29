package com.scholarflow.data.repository;

import java.util.List;
import java.util.UUID;

import com.scholarflow.business.model.PaperComment;

public interface PaperCommentRepository {
    void save(PaperComment comment);
    List<PaperComment> findByPaper(UUID paperId);
    void delete(UUID id);
}
