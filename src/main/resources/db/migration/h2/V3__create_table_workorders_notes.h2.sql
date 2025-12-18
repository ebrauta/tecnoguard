CREATE TABLE tb_workorder_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workorder_id BIGINT,
    message TEXT NOT NULL,
    author VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    created_by TEXT,
    updated_at TIMESTAMP,
    updated_by TEXT,
    FOREIGN KEY (workorder_id) REFERENCES tb_workorders(id)
);