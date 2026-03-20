CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE fields (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name_en     VARCHAR(100) NOT NULL,
    name_sk     VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(20) NOT NULL,
    field_id      UUID REFERENCES fields(id) ON DELETE SET NULL,
    is_active     BOOLEAN DEFAULT true,
    created_at    TIMESTAMP DEFAULT NOW(),

    CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'RESEARCHER', 'REVIEWER'))
);

CREATE TABLE papers (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title         VARCHAR(300) NOT NULL,
    abstract      TEXT NOT NULL,
    keywords      VARCHAR(500),
    doi           VARCHAR(100) UNIQUE,
    field_id      UUID NOT NULL REFERENCES fields(id) ON DELETE RESTRICT,
    submitter_id  UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    status        VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_at    TIMESTAMP DEFAULT NOW(),
    updated_at    TIMESTAMP DEFAULT NOW(),

    CONSTRAINT chk_status CHECK (status IN (
        'DRAFT', 'SUBMITTED', 'UNDER_REVIEW',
        'ACCEPTED', 'REJECTED',
        'MINOR_REVISION', 'MAJOR_REVISION', 'RESUBMITTED'
    ))
);

CREATE TABLE paper_versions (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paper_id       UUID NOT NULL REFERENCES papers(id) ON DELETE CASCADE,
    version_number INTEGER NOT NULL,
    title          VARCHAR(300) NOT NULL,
    abstract       TEXT NOT NULL,
    content        TEXT,
    created_at     TIMESTAMP DEFAULT NOW(),

    CONSTRAINT uq_paper_version UNIQUE (paper_id, version_number)
);

CREATE TABLE paper_authors (
    paper_id      UUID NOT NULL REFERENCES papers(id) ON DELETE CASCADE,
    user_id       UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    author_order  INTEGER NOT NULL DEFAULT 1,

    PRIMARY KEY (paper_id, user_id),
    CONSTRAINT chk_order CHECK (author_order > 0)
);

CREATE TABLE review_assignments (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paper_id    UUID NOT NULL REFERENCES papers(id) ON DELETE CASCADE,
    reviewer_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    assigned_by UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    assigned_at TIMESTAMP DEFAULT NOW(),
    deadline    DATE NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    CONSTRAINT uq_assignment UNIQUE (paper_id, reviewer_id),
    CONSTRAINT chk_assignment_status CHECK (status IN ('PENDING', 'COMPLETED', 'DECLINED'))
);

CREATE TABLE reviews (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assignment_id UUID NOT NULL UNIQUE REFERENCES review_assignments(id) ON DELETE CASCADE,
    decision      VARCHAR(20) NOT NULL,
    comments      TEXT NOT NULL,
    private_notes TEXT,
    submitted_at  TIMESTAMP DEFAULT NOW(),

    CONSTRAINT chk_decision CHECK (decision IN (
        'ACCEPT', 'MINOR_REVISION', 'MAJOR_REVISION', 'REJECT'
    ))
);

CREATE TABLE status_history (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paper_id   UUID NOT NULL REFERENCES papers(id) ON DELETE CASCADE,
    old_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    changed_by UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    comment    TEXT,
    changed_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE audit_log (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID REFERENCES users(id) ON DELETE SET NULL,
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id   UUID,
    details     TEXT,
    created_at  TIMESTAMP DEFAULT NOW()
);