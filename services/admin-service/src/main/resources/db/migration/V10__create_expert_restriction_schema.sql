CREATE TABLE expert_restrictions (
    id BIGSERIAL PRIMARY KEY,
    business_key UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),

    expert_profile_id BIGINT NOT NULL
        REFERENCES expert_profiles(id)
        ON DELETE CASCADE,

    code VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NOT NULL,

    valid_from DATE,
    valid_to DATE,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT uq_expert_restriction_profile_code
        UNIQUE (expert_profile_id, code),

    CONSTRAINT chk_expert_restriction_validity
        CHECK (
            valid_to IS NULL
            OR valid_from IS NULL
            OR valid_to >= valid_from
        )
);

CREATE INDEX idx_expert_restrictions_profile
    ON expert_restrictions(expert_profile_id);

CREATE INDEX idx_expert_restrictions_active
    ON expert_restrictions(active);
