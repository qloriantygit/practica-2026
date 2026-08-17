CREATE TABLE admin_audit_logs (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    correlation_id VARCHAR(100) NOT NULL,
    actor VARCHAR(255) NOT NULL,

    http_method VARCHAR(16) NOT NULL,
    request_path VARCHAR(500) NOT NULL,
    action VARCHAR(600) NOT NULL,

    entity_type VARCHAR(100),
    entity_key VARCHAR(100),

    before_state TEXT,
    after_state TEXT,

    success BOOLEAN NOT NULL,
    error_message TEXT,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT uq_admin_audit_logs_business_key
        UNIQUE (business_key)
);

CREATE INDEX idx_admin_audit_logs_created_at
    ON admin_audit_logs (created_at DESC);

CREATE INDEX idx_admin_audit_logs_actor
    ON admin_audit_logs (actor);

CREATE INDEX idx_admin_audit_logs_correlation_id
    ON admin_audit_logs (correlation_id);

CREATE INDEX idx_admin_audit_logs_entity
    ON admin_audit_logs (entity_type, entity_key);

CREATE INDEX idx_admin_audit_logs_success
    ON admin_audit_logs (success);


CREATE TABLE outbox_events (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    event_id UUID NOT NULL,

    event_type VARCHAR(100) NOT NULL,
    event_version VARCHAR(20) NOT NULL,

    correlation_id VARCHAR(100) NOT NULL,

    source VARCHAR(100) NOT NULL,
    actor_id VARCHAR(255),

    entity_id VARCHAR(100),

    routing_key VARCHAR(150) NOT NULL,

    payload TEXT NOT NULL,

    status VARCHAR(32) NOT NULL,

    retry_count INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMPTZ NOT NULL,

    sent_at TIMESTAMPTZ,
    last_error TEXT,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT uq_outbox_events_business_key
        UNIQUE (business_key),

    CONSTRAINT uq_outbox_events_event_id
        UNIQUE (event_id),

    CONSTRAINT chk_outbox_events_status
        CHECK (
            status IN (
                'PENDING',
                'FAILED',
                'SENT',
                'DEAD'
            )
        ),

    CONSTRAINT chk_outbox_events_retry_count
        CHECK (retry_count >= 0)
);

CREATE INDEX idx_outbox_events_ready
    ON outbox_events (
        status,
        next_attempt_at,
        created_at
    );

CREATE INDEX idx_outbox_events_correlation
    ON outbox_events (correlation_id);

CREATE INDEX idx_outbox_events_event_type
    ON outbox_events (event_type);
