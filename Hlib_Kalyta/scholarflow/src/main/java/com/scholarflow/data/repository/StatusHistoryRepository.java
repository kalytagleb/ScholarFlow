package com.scholarflow.data.repository;

import java.util.List;
import java.util.UUID;

import com.scholarflow.business.model.StatusChange;

public interface StatusHistoryRepository {
    void save(StatusChange change);
    List<StatusChange> findAllByPaper(UUID paperId);
}