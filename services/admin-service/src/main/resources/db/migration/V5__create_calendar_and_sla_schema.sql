CREATE TABLE work_calendars (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    timezone VARCHAR(100) NOT NULL,

    working_days JSONB NOT NULL,

    workday_start TIME NOT NULL,
    workday_end TIME NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT uq_work_calendars_business_key
        UNIQUE (business_key),

    CONSTRAINT chk_work_calendars_code
        CHECK (BTRIM(code) <> ''),

    CONSTRAINT chk_work_calendars_name
        CHECK (BTRIM(name) <> ''),

    CONSTRAINT chk_work_calendars_hours
        CHECK (workday_end > workday_start)
);

CREATE UNIQUE INDEX uq_work_calendars_code_ci
    ON work_calendars (LOWER(code));

CREATE INDEX idx_work_calendars_active
    ON work_calendars (active);


CREATE TABLE work_calendar_exceptions (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    calendar_id BIGINT NOT NULL,

    exception_date DATE NOT NULL,

    working_day BOOLEAN NOT NULL,

    workday_start TIME,
    workday_end TIME,

    description TEXT,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT fk_calendar_exceptions_calendar
        FOREIGN KEY (calendar_id)
        REFERENCES work_calendars (id)
        ON DELETE CASCADE,

    CONSTRAINT uq_calendar_exceptions_business_key
        UNIQUE (business_key),

    CONSTRAINT uq_calendar_exception_date
        UNIQUE (calendar_id, exception_date),

    CONSTRAINT chk_calendar_exception_hours
        CHECK (
            workday_start IS NULL
            OR workday_end IS NULL
            OR workday_end > workday_start
        )
);

CREATE INDEX idx_calendar_exceptions_calendar
    ON work_calendar_exceptions (calendar_id);

CREATE INDEX idx_calendar_exceptions_date
    ON work_calendar_exceptions (exception_date);


CREATE TABLE sla_policies (
    id BIGSERIAL PRIMARY KEY,

    business_key UUID NOT NULL DEFAULT gen_random_uuid(),

    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    response_minutes INTEGER NOT NULL,
    resolution_minutes INTEGER NOT NULL,

    calendar_id BIGINT NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT fk_sla_policies_calendar
        FOREIGN KEY (calendar_id)
        REFERENCES work_calendars (id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_sla_policies_business_key
        UNIQUE (business_key),

    CONSTRAINT chk_sla_policy_code
        CHECK (BTRIM(code) <> ''),

    CONSTRAINT chk_sla_policy_name
        CHECK (BTRIM(name) <> ''),

    CONSTRAINT chk_sla_response_minutes
        CHECK (response_minutes > 0),

    CONSTRAINT chk_sla_resolution_minutes
        CHECK (
            resolution_minutes > 0
            AND resolution_minutes >= response_minutes
        )
);

CREATE UNIQUE INDEX uq_sla_policies_code_ci
    ON sla_policies (LOWER(code));

CREATE INDEX idx_sla_policies_active
    ON sla_policies (active);

CREATE INDEX idx_sla_policies_calendar
    ON sla_policies (calendar_id);
