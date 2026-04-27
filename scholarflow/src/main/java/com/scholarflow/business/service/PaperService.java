package com.scholarflow.business.service;

import java.util.Objects;
import java.util.UUID;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.PaperStatus;
import com.scholarflow.business.model.PaperVersion;
import com.scholarflow.business.model.StatusChange;
import com.scholarflow.business.model.User;
import com.scholarflow.data.repository.PaperRepository;
import com.scholarflow.data.repository.PaperVersionRepository;
import com.scholarflow.data.repository.StatusHistoryRepository;

public final class PaperService {
    private final PaperRepository paperRepo;
    private final PaperVersionRepository versionRepo;
    private final StatusHistoryRepository historyRepo;

    public PaperService(
        final PaperRepository paperRepo,
        final PaperVersionRepository versionRepo,
        final StatusHistoryRepository historyRepo
    ) {
        this.paperRepo = Objects.requireNonNull(paperRepo);
        this.versionRepo = Objects.requireNonNull(versionRepo);
        this.historyRepo = Objects.requireNonNull(historyRepo);
    }

    public Paper createPaper(
        final String title,
        final String paperAbstract,
        final String keywords,
        final UUID fiedldId,
        final User author
    ) {
        // Check permissions
        if (!author.canSubmitPapers()) {
            throw new IllegalArgumentException("User does not have permission to submit papers");
        }

        // Create and save paper
        final Paper newPaper = this.paperRepo.save(
            new Paper(title, paperAbstract, keywords, fiedldId, author.id().orElseThrow())
        );

        final UUID paperId = newPaper.id().orElseThrow();

        // Create first version of content
        this.versionRepo.save(
            new PaperVersion(paperId, 1, title, paperAbstract, null)
        );

        this.historyRepo.save(
            new StatusChange(paperId, null, PaperStatus.DRAFT, author.id().get(), "Initial draft created")
        );

        return newPaper;
    }

    public PaperVersion createNewVersion(
        final UUID paperId,
        final String title,
        final String paperAbstract,
        final String content,
        final User author
    ) {
        final Paper paper = this.paperRepo.findById(paperId)
            .orElseThrow(() -> new IllegalArgumentException("Paper not found"));

        if (!paper.submitterId().equals(author.id().orElse(null)) && !author.hasAdminPrivileges()) {
            throw new IllegalArgumentException("Only author can add new versions");
        }

        final int lastNumber = this.versionRepo.latestOf(paperId)
            .map(PaperVersion::number)
            .orElse(0);

        return this.versionRepo.save(
            new PaperVersion(paperId, lastNumber + 1, title, paperAbstract, content)
        );
    }

    public Paper submitPaper(final UUID paperId, final User user) {
        final Paper current = this.paperRepo.findById(paperId)
            .orElseThrow(() -> new IllegalArgumentException("Paper not found"));

        // Try to change status over state-machine in Paper
        final Paper submitted = current.submit();

        // Save changes.
        this.paperRepo.update(submitted);

        this.historyRepo.save(
            new StatusChange(
                paperId,
                current.status(),
                submitted.status(), 
                user.id().orElseThrow(), 
                "Paper submitted for review"
            )
        );

        return submitted;
    }
}
