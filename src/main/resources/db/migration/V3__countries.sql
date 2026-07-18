-- V3 — Countries table + Phase 1 seed (CM, CI, MU)

CREATE TABLE IF NOT EXISTS countries (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    code       VARCHAR(2)   NOT NULL,
    name       VARCHAR(100) NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL,
    CONSTRAINT pk_countries      PRIMARY KEY (id),
    CONSTRAINT uq_countries_code UNIQUE (code)
);

INSERT INTO countries (code, name, active, created_at) VALUES
('CM', 'Cameroun',       TRUE, CURRENT_TIMESTAMP),
('CI', 'Côte d''Ivoire', TRUE, CURRENT_TIMESTAMP),
('MU', 'Maurice',        TRUE, CURRENT_TIMESTAMP);
