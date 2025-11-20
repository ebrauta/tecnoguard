CREATE TABLE tb_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
    created_by TEXT,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by TEXT
);

CREATE TABLE tb_workorder_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workorder_id BIGINT,
    message TEXT NOT NULL,
    author VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by TEXT,
    FOREIGN KEY (workorder_id) REFERENCES tb_workorder(id)
);

CREATE TABLE tb_system_log (
    id_systemlog BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    actor_username TEXT,
    action TEXT NOT NULL,
    target_type TEXT,
    target_id BIGINT,
    details TEXT
);