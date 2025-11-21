INSERT INTO tb_users (
    name,
    username,
    password,
    user_role,
    active,
    created_at,
    updated_at,
    last_login,
    email
) VALUES (
    'Administrador',
    'admin',
    '$2a$12$xGg0gmgUkCEcCN0qoBtfjum4aNXtS.mR9/iuBVlmZcz1S5d6pmC4q',
    'ADMIN',
    TRUE,
    '2025-10-07 00:00:00',
    '2025-10-07 00:00:00',
    '2025-10-07 00:00:00',
    'admin@mail.com'
);