package com.scholarflow.business.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import com.scholarflow.business.model.state.ExceptState;
import com.scholarflow.business.model.state.PaperState;

public final class Paper {
    private static final Pattern TITLE_REGEX = 
        Pattern.compile("^[\\p{L}\\d\\s\\-_:,.()]{5,300}$");

    private static final Pattern KEYWORD_REGEX = 
        Pattern.compile("^[\\p{L}\\d\\s]+(?:,[\\p{L}\\d\\s]+)*$");

    private final Optional<UUID> id;
    private final String title;
    private final String paperAbstract;
    private final Optional<String> keywords;
    private final Optional<String> doi;
    private final UUID fieldId;
    private final UUID submitterId;
    private final PaperState state;
    private final Optional<LocalDateTime> createdAt;
    private final Optional<LocalDateTime> updatedAt;

    // For loading from DB
    public Paper(
        final UUID id, 
        final String title, 
        final String paperAbstract, 
        final String keywords, 
        final String doi,
        final UUID fieldId, 
        final UUID submitterId, 
        final PaperStatus status, 
        final LocalDateTime createdAt, 
        final LocalDateTime updatedAt
    ) {
        this.id = Optional.of(Objects.requireNonNull(id));
        this.title = this.validateTitle(title);
        this.paperAbstract = Objects.requireNonNull(paperAbstract);
        this.keywords = this.validateKeywords(keywords);
        this.doi = Optional.ofNullable(doi);
        this.fieldId = Objects.requireNonNull(fieldId);
        this.submitterId = Objects.requireNonNull(submitterId);
        this.state = this.identifyState(status);
        this.createdAt = Optional.of(Objects.requireNonNull(createdAt));
        this.updatedAt = Optional.of(Objects.requireNonNull(updatedAt));
    }

    // For creating new Paper (Draft)
    public Paper(
        final String title, 
        final String paperAbstract, 
        final String keywords, 
        final UUID fieldId, 
        final UUID submitterId
    ) {
        this.id = Optional.empty();
        this.title = this.validateTitle(title);
        this.paperAbstract = Objects.requireNonNull(paperAbstract);
        this.keywords = this.validateKeywords(keywords);
        this.doi = Optional.empty();
        this.fieldId = Objects.requireNonNull(fieldId);
        this.submitterId = Objects.requireNonNull(submitterId);
        this.state = new DraftState(this); // New paper always in DRAFT
        this.createdAt = Optional.empty();
        this.updatedAt = Optional.empty();
    }

    private PaperState identifyState(final PaperStatus status) {
        return switch (status) {
            case DRAFT -> new DraftState(this);
            case SUBMITTED -> new SubmittedState(this);
            case UNDER_REVIEW -> new UnderReviewState(this);
            case ACCEPTED -> new AcceptedState(this);
            case REJECTED -> new RejectedState(this);
            case MINOR_REVISION -> new MinorRevisionState(this);
            case MAJOR_REVISION -> new MajorRevisionState(this);
            case RESUBMITTED -> new ResubmittedState(this);
            default -> new ExceptState(status);
        };
    }

    // Create copy of object with new status 
    public Paper withStatus(final PaperStatus next) {
        return new Paper(
            this.id.orElse(null),
            this.title, 
            this.paperAbstract,
            this.keywords.orElse(null), 
            this.doi.orElse(null), 
            this.fieldId, 
            this.submitterId,
            next,
            this.createdAt.orElse(null), 
            LocalDateTime.now()
        );
    }

    private String validateTitle(final String text) {
        if (text == null || !TITLE_REGEX.matcher(text).matches()) {
            throw new IllegalArgumentException("Title must be between 5 and 300 characters.");
        }
        return text;
    }

    private Optional<String> validateKeywords(final String kw) {
        if (kw != null && !kw.isBlank() && !KEYWORD_REGEX.matcher(kw).matches()) {
            throw new IllegalArgumentException("Keyword must be comma-separated words.");
        }
        return Optional.ofNullable(kw);
    }

    // Behavior
    public Paper submit() {
        return this.state.submit();
    }

    public Paper startReview() {
        return this.state.startReview();
    }

    public Paper accept() {
        return this.state.accept();
    }

    public Paper reject() {
        return this.state.reject();
    }

    public Optional<UUID> id() { return id; }
    public String title() { return title; }
    public String paperAbstract() { return paperAbstract; }
    public Optional<String> keywords() { return keywords; }
    public Optional<String> doi() { return doi; }
    public UUID fieldId() { return fieldId; }
    public UUID submitterId() { return submitterId; }
    public PaperStatus status() { return state.status(); }
    public Optional<LocalDateTime> createdAt() { return createdAt; }
    public Optional<LocalDateTime> updatedAt() { return updatedAt; }
}