CREATE TABLE tb_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    user_role VARCHAR(20) NOT NULL,
    last_login TIMESTAMP WITHOUT TIME ZONE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE tb_workorder (
    id BIGSERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    equipment VARCHAR(255),
    client VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    assigned_technician VARCHAR(255),
    priority VARCHAR(10) NOT NULL,
    scheduled_date DATE,
    opening_date TIMESTAMP WITHOUT TIME ZONE,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    closing_date TIMESTAMP WITHOUT TIME ZONE,
    cancel_date TIMESTAMP WITHOUT TIME ZONE,
    cancel_reason TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by TEXT NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by TEXT
);

CREATE TABLE tb_workorder_notes (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL REFERENCES tb_workorder (id),
    message TEXT NOT NULL,
    author VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by TEXT NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by TEXT
);

CREATE TABLE tb_system_log (
    id_systemlog BIGSERIAL PRIMARY KEY,
    log_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    actor_username TEXT NOT NULL,
    action TEXT NOT NULL,
    target_type TEXT NOT NULL,
    target_id BIGINT NOT NULL,
    details TEXT NOT NULL
);