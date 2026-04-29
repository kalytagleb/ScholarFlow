package com.scholarflow.data.repository;

import java.util.List;
import java.util.UUID;

import com.scholarflow.business.model.PaperAuthor;

public interface PaperAuthorRepository {
    List<PaperAuthor> findByPaper(UUID paperId);
    List<PaperAuthor> findByUser(UUID userId);
    void save(PaperAuthor author);
    void remove(UUID paperId, UUID userId);
    void removeAllByPaper(UUID paperId);
}
