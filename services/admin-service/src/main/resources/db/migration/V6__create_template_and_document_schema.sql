CREATE TABLE admin_templates (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    template_type VARCHAR(32) NOT NULL,
    channel VARCHAR(32),

    subject VARCHAR(500),
    body TEXT NOT NULL,

    variables JSONB NOT NULL DEFAULT '[]'::jsonb,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT uq_admin_templates_business_key
        UNIQUE (business_key),

    CONSTRAINT chk_admin_templates_code
        CHECK (BTRIM(code) <> ''),

    CONSTRAINT chk_admin_templates_name
        CHECK (BTRIM(name) <> ''),

    CONSTRAINT chk_admin_templates_body
        CHECK (BTRIM(body) <> ''),

    CONSTRAINT chk_admin_templates_type
        CHECK (
            template_type IN (
                'NOTIFICATION',
                'DOCUMENT'
            )
        ),

    CONSTRAINT chk_admin_templates_channel
        CHECK (
            channel IS NULL
            OR channel IN (
                'EMAIL',
                'SMS',
                'PUSH',
                'IN_APP'
            )
        )
);

CREATE UNIQUE INDEX uq_admin_templates_code_ci
    ON admin_templates (LOWER(code));

CREATE INDEX idx_admin_templates_type
    ON admin_templates (template_type);

CREATE INDEX idx_admin_templates_active
    ON admin_templates (active);


CREATE TABLE document_types (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT uq_document_types_business_key
        UNIQUE (business_key),

    CONSTRAINT chk_document_types_code
        CHECK (BTRIM(code) <> ''),

    CONSTRAINT chk_document_types_name
        CHECK (BTRIM(name) <> '')
);

CREATE UNIQUE INDEX uq_document_types_code_ci
    ON document_types (LOWER(code));

CREATE INDEX idx_document_types_active
    ON document_types (active);


CREATE TABLE mandatory_document_rules (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    context_code VARCHAR(100) NOT NULL,

    document_type_id BIGINT NOT NULL,

    mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT fk_mandatory_document_rules_type
        FOREIGN KEY (document_type_id)
        REFERENCES document_types (id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_mandatory_document_rules_business_key
        UNIQUE (business_key),

    CONSTRAINT uq_mandatory_document_rule
        UNIQUE (
            context_code,
            document_type_id
        ),

    CONSTRAINT chk_mandatory_document_rules_context
        CHECK (BTRIM(context_code) <> '')
);

CREATE INDEX idx_mandatory_document_rules_context
    ON mandatory_document_rules (context_code);

CREATE INDEX idx_mandatory_document_rules_active
    ON mandatory_document_rules (active);
