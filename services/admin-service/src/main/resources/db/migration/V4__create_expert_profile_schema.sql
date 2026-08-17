CREATE TABLE expert_profiles (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    user_id BIGINT NOT NULL,

    specialization VARCHAR(255) NOT NULL,
    bio TEXT,

    status VARCHAR(32) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT fk_expert_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES user_accounts (id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_expert_profiles_business_key
        UNIQUE (business_key),

    CONSTRAINT uq_expert_profiles_user
        UNIQUE (user_id),

    CONSTRAINT chk_expert_profiles_specialization
        CHECK (BTRIM(specialization) <> ''),

    CONSTRAINT chk_expert_profiles_status
        CHECK (
            status IN (
                'ACTIVE',
                'INACTIVE'
            )
        )
);

CREATE INDEX idx_expert_profiles_status
    ON expert_profiles (status);

CREATE INDEX idx_expert_profiles_available
    ON expert_profiles (available);


CREATE TABLE expert_competencies (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    expert_profile_id BIGINT NOT NULL,
    directory_item_id BIGINT NOT NULL,

    proficiency_level INTEGER,
    note TEXT,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT fk_expert_competencies_profile
        FOREIGN KEY (expert_profile_id)
        REFERENCES expert_profiles (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_expert_competencies_directory_item
        FOREIGN KEY (directory_item_id)
        REFERENCES directory_items (id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_expert_competencies_business_key
        UNIQUE (business_key),

    CONSTRAINT uq_expert_competencies_profile_item
        UNIQUE (
            expert_profile_id,
            directory_item_id
        ),

    CONSTRAINT chk_expert_competencies_level
        CHECK (
            proficiency_level IS NULL
            OR proficiency_level BETWEEN 1 AND 5
        )
);

CREATE INDEX idx_expert_competencies_profile
    ON expert_competencies (expert_profile_id);

CREATE INDEX idx_expert_competencies_directory_item
    ON expert_competencies (directory_item_id);
