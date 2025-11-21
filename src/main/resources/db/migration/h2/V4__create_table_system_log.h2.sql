CREATE TABLE tb_system_log (
    id_systemlog BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_timestamp TIMESTAMP NOT NULL,
    actor_username TEXT,
    action TEXT NOT NULL,
    target_type TEXT,
    target_id BIGINT,
    details TEXT
);