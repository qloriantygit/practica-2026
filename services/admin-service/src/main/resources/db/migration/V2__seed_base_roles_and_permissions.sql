INSERT INTO roles
(
    code,
    name,
    description,
    system_role,
    created_by,
    updated_by
)
VALUES
    ('ADMIN', 'Администратор', 'Полный административный доступ к системе', TRUE, 'SYSTEM', 'SYSTEM'),
    ('METHODOLOGIST', 'Методолог', 'Работа со справочниками, компетенциями и шаблонами', TRUE, 'SYSTEM', 'SYSTEM'),
    ('MANAGER', 'Руководитель', 'Согласование изменений и управление процессами', TRUE, 'SYSTEM', 'SYSTEM'),
    ('ANALYST', 'Аналитик', 'Просмотр административных данных, аудита и отчетов', TRUE, 'SYSTEM', 'SYSTEM');


INSERT INTO permissions
(
    code,
    name,
    description
)
VALUES
    ('USER_READ', 'Просмотр пользователей', 'Просмотр пользователей системы'),
    ('USER_MANAGE', 'Управление пользователями', 'Создание и изменение пользователей'),

    ('ROLE_READ', 'Просмотр ролей', 'Просмотр ролей и разрешений'),
    ('ROLE_MANAGE', 'Управление ролями', 'Создание ролей и назначение полномочий'),

    ('ORGANIZATION_READ', 'Просмотр организаций', 'Просмотр структуры организаций'),
    ('ORGANIZATION_MANAGE', 'Управление организациями', 'Создание и изменение организаций'),

    ('DIRECTORY_READ', 'Просмотр справочников', 'Просмотр НСИ и версий справочников'),
    ('DIRECTORY_MANAGE', 'Управление справочниками', 'Создание и изменение НСИ'),
    ('DIRECTORY_PUBLISH', 'Публикация справочников', 'Публикация согласованных версий НСИ'),

    ('EXPERT_READ', 'Просмотр экспертов', 'Просмотр профилей экспертов'),
    ('EXPERT_MANAGE', 'Управление экспертами', 'Создание и изменение профилей экспертов'),

    ('SLA_READ', 'Просмотр SLA', 'Просмотр календарей и правил SLA'),
    ('SLA_MANAGE', 'Управление SLA', 'Настройка календарей и правил SLA'),

    ('TEMPLATE_READ', 'Просмотр шаблонов', 'Просмотр шаблонов уведомлений'),
    ('TEMPLATE_MANAGE', 'Управление шаблонами', 'Создание и изменение шаблонов'),

    ('DOCUMENT_READ', 'Просмотр типов документов', 'Просмотр типов документов и требований'),
    ('DOCUMENT_MANAGE', 'Управление типами документов', 'Изменение типов документов и матрицы обязательности'),

    ('APPROVAL_READ', 'Просмотр согласований', 'Просмотр заявок на согласование'),
    ('APPROVAL_MANAGE', 'Управление согласованиями', 'Одобрение и отклонение изменений'),

    ('AUDIT_READ', 'Просмотр аудита', 'Просмотр журнала административных действий');


INSERT INTO role_permissions
(
    role_id,
    permission_id,
    granted_by
)
SELECT
    r.id,
    p.id,
    'SYSTEM'
FROM roles r
CROSS JOIN permissions p
WHERE r.code = 'ADMIN';


INSERT INTO role_permissions
(
    role_id,
    permission_id,
    granted_by
)
SELECT
    r.id,
    p.id,
    'SYSTEM'
FROM roles r
JOIN permissions p
    ON p.code IN
    (
        'ORGANIZATION_READ',
        'DIRECTORY_READ',
        'DIRECTORY_MANAGE',
        'EXPERT_READ',
        'EXPERT_MANAGE',
        'SLA_READ',
        'TEMPLATE_READ',
        'TEMPLATE_MANAGE',
        'DOCUMENT_READ',
        'DOCUMENT_MANAGE'
    )
WHERE r.code = 'METHODOLOGIST';


INSERT INTO role_permissions
(
    role_id,
    permission_id,
    granted_by
)
SELECT
    r.id,
    p.id,
    'SYSTEM'
FROM roles r
JOIN permissions p
    ON p.code IN
    (
        'USER_READ',
        'ROLE_READ',
        'ORGANIZATION_READ',
        'DIRECTORY_READ',
        'DIRECTORY_PUBLISH',
        'EXPERT_READ',
        'SLA_READ',
        'SLA_MANAGE',
        'APPROVAL_READ',
        'APPROVAL_MANAGE',
        'AUDIT_READ'
    )
WHERE r.code = 'MANAGER';


INSERT INTO role_permissions
(
    role_id,
    permission_id,
    granted_by
)
SELECT
    r.id,
    p.id,
    'SYSTEM'
FROM roles r
JOIN permissions p
    ON p.code IN
    (
        'USER_READ',
        'ROLE_READ',
        'ORGANIZATION_READ',
        'DIRECTORY_READ',
        'EXPERT_READ',
        'SLA_READ',
        'TEMPLATE_READ',
        'DOCUMENT_READ',
        'APPROVAL_READ',
        'AUDIT_READ'
    )
WHERE r.code = 'ANALYST';
