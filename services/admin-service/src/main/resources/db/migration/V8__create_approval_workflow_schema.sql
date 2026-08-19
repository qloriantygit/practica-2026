CREATE TABLE approval_requests (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    resource_type VARCHAR(64) NOT NULL,
    resource_key UUID NOT NULL,

    status VARCHAR(32) NOT NULL,

    requested_by VARCHAR(255) NOT NULL,
    requested_at TIMESTAMPTZ NOT NULL,

    decided_by VARCHAR(255),
    decided_at TIMESTAMPTZ,
    decision_comment TEXT,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT uq_approval_requests_business_key
        UNIQUE (business_key),

    CONSTRAINT chk_approval_resource_type
        CHECK (
            resource_type IN (
                'DIRECTORY_VERSION'
            )
        ),

    CONSTRAINT chk_approval_status
        CHECK (
            status IN (
                'PENDING',
                'APPROVED',
                'REJECTED'
            )
        )
);

CREATE UNIQUE INDEX uq_approval_pending_resource
    ON approval_requests (
        resource_type,
        resource_key
    )
    WHERE status = 'PENDING';

CREATE INDEX idx_approval_requests_status
    ON approval_requests (status);

CREATE INDEX idx_approval_requests_resource
    ON approval_requests (
        resource_type,
        resource_key
    );

CREATE INDEX idx_approval_requests_requested_at
    ON approval_requests (requested_at DESC);
