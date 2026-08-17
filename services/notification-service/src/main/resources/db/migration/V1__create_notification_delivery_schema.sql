CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE notification_deliveries (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    event_id UUID NOT NULL,

    correlation_id VARCHAR(100) NOT NULL,

    event_version VARCHAR(20) NOT NULL,

    channel VARCHAR(32) NOT NULL,

    recipient VARCHAR(500) NOT NULL,

    subject VARCHAR(1000),
    body TEXT NOT NULL,

    status VARCHAR(32) NOT NULL,

    attempt_count INTEGER NOT NULL DEFAULT 0,

    received_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMPTZ,

    last_error TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_notification_deliveries_business_key
        UNIQUE (business_key),

    CONSTRAINT uq_notification_deliveries_event_id
        UNIQUE (event_id),

    CONSTRAINT chk_notification_delivery_channel
        CHECK (
            channel IN (
                'EMAIL',
                'SMS',
                'PUSH',
                'IN_APP'
            )
        ),

    CONSTRAINT chk_notification_delivery_status
        CHECK (
            status IN (
                'RECEIVED',
                'SENT',
                'FAILED'
            )
        ),

    CONSTRAINT chk_notification_attempt_count
        CHECK (attempt_count >= 0)
);

CREATE INDEX idx_notification_deliveries_correlation
    ON notification_deliveries (correlation_id);

CREATE INDEX idx_notification_deliveries_status
    ON notification_deliveries (status);

CREATE INDEX idx_notification_deliveries_received
    ON notification_deliveries (received_at DESC);
