CREATE TABLE saved_views (
    id BIGSERIAL PRIMARY KEY,
    business_key UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),

    owner_user_id BIGINT NOT NULL
        REFERENCES user_accounts(id)
        ON DELETE CASCADE,

    name VARCHAR(150) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,

    filters JSONB NOT NULL DEFAULT '{}'::jsonb,

    sort_by VARCHAR(100),
    sort_direction VARCHAR(4),

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT uq_saved_view_owner_resource_name
        UNIQUE (owner_user_id, resource_type, name),

    CONSTRAINT chk_saved_view_sort_direction
        CHECK (
            sort_direction IS NULL
            OR sort_direction IN ('ASC', 'DESC')
        )
);

CREATE INDEX idx_saved_views_owner
    ON saved_views(owner_user_id);

CREATE INDEX idx_saved_views_owner_resource
    ON saved_views(owner_user_id, resource_type);
