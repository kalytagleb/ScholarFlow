package com.scholarflow.data.repository;

import java.util.UUID;

import com.scholarflow.business.model.PaperLike;

public interface PaperLikeRepository {
    void save(PaperLike like);
    void delete(UUID paperId, UUID userId);
    long countByPaper(UUID paperId);
    boolean exists(UUID paperId, UUID userId);
}
