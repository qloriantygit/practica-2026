CREATE TABLE directories (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT uq_directories_business_key
        UNIQUE (business_key),

    CONSTRAINT chk_directories_code_not_blank
        CHECK (BTRIM(code) <> ''),

    CONSTRAINT chk_directories_name_not_blank
        CHECK (BTRIM(name) <> '')
);

CREATE UNIQUE INDEX uq_directories_code_ci
    ON directories (LOWER(code));


CREATE TABLE directory_versions (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    directory_id BIGINT NOT NULL,

    version_number INTEGER NOT NULL,

    status VARCHAR(32) NOT NULL,

    valid_from DATE,
    valid_to DATE,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT fk_directory_versions_directory
        FOREIGN KEY (directory_id)
        REFERENCES directories (id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_directory_versions_business_key
        UNIQUE (business_key),

    CONSTRAINT uq_directory_versions_number
        UNIQUE (directory_id, version_number),

    CONSTRAINT chk_directory_versions_version_number
        CHECK (version_number > 0),

    CONSTRAINT chk_directory_versions_status
        CHECK (
            status IN (
                'DRAFT',
                'ON_APPROVAL',
                'PUBLISHED',
                'ARCHIVED'
            )
        ),

    CONSTRAINT chk_directory_versions_validity
        CHECK (
            valid_to IS NULL
            OR valid_from IS NULL
            OR valid_to >= valid_from
        )
);

CREATE INDEX idx_directory_versions_directory
    ON directory_versions (directory_id);

CREATE INDEX idx_directory_versions_status
    ON directory_versions (status);

CREATE INDEX idx_directory_versions_validity
    ON directory_versions (valid_from, valid_to);


CREATE TABLE directory_items (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    directory_version_id BIGINT NOT NULL,

    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    sort_order INTEGER NOT NULL DEFAULT 0,

    attributes JSONB NOT NULL DEFAULT '{}'::jsonb,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT fk_directory_items_version
        FOREIGN KEY (directory_version_id)
        REFERENCES directory_versions (id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_directory_items_business_key
        UNIQUE (business_key),

    CONSTRAINT chk_directory_items_code_not_blank
        CHECK (BTRIM(code) <> ''),

    CONSTRAINT chk_directory_items_name_not_blank
        CHECK (BTRIM(name) <> ''),

    CONSTRAINT chk_directory_items_sort_order
        CHECK (sort_order >= 0)
);

CREATE UNIQUE INDEX uq_directory_items_version_code_ci
    ON directory_items (
        directory_version_id,
        LOWER(code)
    );

CREATE INDEX idx_directory_items_version
    ON directory_items (directory_version_id);

CREATE INDEX idx_directory_items_code
    ON directory_items (code);

CREATE INDEX idx_directory_items_attributes_gin
    ON directory_items
    USING GIN (attributes);
