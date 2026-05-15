package com.scholarflow.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.PaperVersion;

public interface PaperVersionRepository {
    PaperVersion save(PaperVersion version);
    Optional<PaperVersion> latestOf(UUID paperId);
    List<PaperVersion> allOf(UUID paperId);
}