-- =============================================================================
-- MEMBERS
-- =============================================================================
CREATE TABLE members (
    id      BIGSERIAL    PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    role    VARCHAR(50)  NOT NULL
);

-- =============================================================================
-- PROJECTS
-- =============================================================================
CREATE TABLE projects (
    id                BIGSERIAL       PRIMARY KEY,
    name              VARCHAR(255)    NOT NULL,
    start_date        DATE,
    expected_end_date DATE,
    actual_end_date   DATE,
    total_budget      NUMERIC(15, 2),
    description       TEXT,
    manager_id        BIGINT          NOT NULL,
    status            VARCHAR(50)     NOT NULL DEFAULT 'EM_ANALISE',

    CONSTRAINT fk_project_manager
        FOREIGN KEY (manager_id) REFERENCES members(id)
);

-- =============================================================================
-- PROJECT_MEMBERS (tabela associativa N:N)
-- =============================================================================
CREATE TABLE project_members (
    project_id BIGINT NOT NULL,
    member_id  BIGINT NOT NULL,

    PRIMARY KEY (project_id, member_id),

    CONSTRAINT fk_pm_project
        FOREIGN KEY (project_id) REFERENCES projects(id),

    CONSTRAINT fk_pm_member
        FOREIGN KEY (member_id) REFERENCES members(id)
);

-- =============================================================================
-- INDEXES
-- =============================================================================
CREATE INDEX idx_projects_status     ON projects(status);
CREATE INDEX idx_projects_manager    ON projects(manager_id);
CREATE INDEX idx_project_members_member ON project_members(member_id);