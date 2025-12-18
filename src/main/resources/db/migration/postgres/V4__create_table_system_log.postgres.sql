CREATE TABLE tb_system_log (
                               id_systemlog BIGSERIAL PRIMARY KEY,
                               log_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               actor_username TEXT NOT NULL,
                               action TEXT NOT NULL,
                               target_type TEXT NOT NULL,
                               target_id BIGINT NOT NULL,
                               details TEXT NOT NULL
);

CREATE INDEX idx_system_log_timestamp ON tb_system_log (log_timestamp);
CREATE INDEX idx_system_log_actor ON tb_system_log (actor_username);