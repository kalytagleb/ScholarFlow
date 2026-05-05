package com.scholarflow.business.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.model.PaperVersion;
import com.scholarflow.business.model.StatusChange;
import com.scholarflow.business.model.User;
import com.scholarflow.business.model.enums.PaperStatus;
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
        final UUID fieldId,
        final User author
    ) {
        // Check permissions
        if (!author.canSubmitPapers()) {
            throw new IllegalArgumentException("User does not have permission to submit papers");
        }

        // Create and save paper
        final Paper newPaper = this.paperRepo.save(
            new Paper(title, paperAbstract, keywords, fieldId, author.id().orElseThrow())
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

        if (!current.submitterId().equals(user.id().orElse(null)) && !user.hasAdminPrivileges()) {
            throw new IllegalArgumentException("Only the author or admin can submit a paper");
        }

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

    public Paper requestRevision(final UUID paperId, final boolean major, final User admin) {
        if (!admin.hasAdminPrivileges()) {
            throw new IllegalArgumentException("Only admin can request revision");
        }

        final Paper current = this.paperRepo.findById(paperId)
            .orElseThrow(() -> new IllegalArgumentException("Paper not found"));

        final Paper revised = current.requestRevision(major);

        this.paperRepo.update(revised);

        this.historyRepo.save(new StatusChange(
            paperId,
            current.status(),
            revised.status(),
            admin.id().orElseThrow(),
            major ? "Major revision requested" : "Minor revision requested"
        ));

        return revised;
    }

    public Paper resubmitPaper(final UUID paperId, final User user) {
        final Paper current = this.paperRepo.findById(paperId)
            .orElseThrow(() -> new IllegalArgumentException("Paper not found"));

        if (!current.submitterId().equals(user.id().orElse(null)) && !user.hasAdminPrivileges()) {
            throw new IllegalArgumentException("Only the author or admin can resubmit a paper");
        }

        final Paper resubmitted = current.resubmit();

        this.paperRepo.update(resubmitted);

        this.historyRepo.save(new StatusChange(
            paperId,
            current.status(),
            resubmitted.status(),
            user.id().orElseThrow(),
            "Paper resubmitted after revision"
        ));

        return resubmitted;
    }

    // For READER. Shows checked articles
    public List<Paper> findPublishedPapers() {
        return paperRepo.findAllByStatus(PaperStatus.ACCEPTED);
    }

    // For ADMINS. 
    public List<Paper> findAllForAdmin(User admin) {
        if (!admin.hasAdminPrivileges()) {
            throw new IllegalArgumentException("Access denied");
        }

        return paperRepo.findAll();
    }

    public List<Paper> findByAuthor(UUID authorId) {
        return paperRepo.findAllBySubmitter(authorId);
    }

    public Optional<Paper> findById(UUID id) {
        return paperRepo.findById(id);
    }
}
