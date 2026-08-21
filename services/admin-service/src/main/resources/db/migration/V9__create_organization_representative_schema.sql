CREATE TABLE organization_representatives (
    id BIGSERIAL PRIMARY KEY,
    business_key UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),

    organization_id BIGINT NOT NULL
        REFERENCES organizations(id),

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    position VARCHAR(200) NOT NULL,

    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT uk_organization_representative_email
        UNIQUE (organization_id, email)
);

CREATE INDEX idx_org_representatives_organization
    ON organization_representatives(organization_id);

CREATE INDEX idx_org_representatives_active
    ON organization_representatives(active);
