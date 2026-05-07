-- Alinea tabla usuario con la entidad JPA actual

-- nombre
SET @c := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'usuario' AND COLUMN_NAME = 'nombre'
);
SET @sql := IF(@c = 0,
    'ALTER TABLE usuario ADD COLUMN nombre VARCHAR(120) NULL',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- email
SET @c := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'usuario' AND COLUMN_NAME = 'email'
);
SET @sql := IF(@c = 0,
    'ALTER TABLE usuario ADD COLUMN email VARCHAR(150) NULL',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- id_comercio
SET @c := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'usuario' AND COLUMN_NAME = 'id_comercio'
);
SET @sql := IF(@c = 0,
    'ALTER TABLE usuario ADD COLUMN id_comercio BIGINT NULL',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Backfill desde persona/username en esquemas heredados
UPDATE usuario u
LEFT JOIN persona p ON p.id_persona = u.id_persona
SET
    u.email = COALESCE(u.email, p.email, CONCAT(u.username, '@detubarrio.local')),
    u.nombre = COALESCE(u.nombre, CONCAT(COALESCE(p.nombre, ''), CASE WHEN p.apellidos IS NULL THEN '' ELSE ' ' END, COALESCE(p.apellidos, '')), u.username);

-- Fallback defensivo
UPDATE usuario SET email = CONCAT('user', id_usuario, '@detubarrio.local') WHERE email IS NULL OR TRIM(email) = '';
UPDATE usuario SET nombre = CONCAT('Usuario ', id_usuario) WHERE nombre IS NULL OR TRIM(nombre) = '';

-- Ajustar nullability conforme entidad
ALTER TABLE usuario MODIFY COLUMN email VARCHAR(150) NOT NULL;
ALTER TABLE usuario MODIFY COLUMN nombre VARCHAR(120) NOT NULL;

-- Unicidad email
SET @idx := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'usuario' AND INDEX_NAME = 'uk_usuario_email'
);
SET @sql := IF(@idx = 0,
    'CREATE UNIQUE INDEX uk_usuario_email ON usuario(email)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- FK usuario -> comercio
SET @fk := (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'usuario'
      AND CONSTRAINT_NAME = 'fk_usuario_comercio'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
    'ALTER TABLE usuario ADD CONSTRAINT fk_usuario_comercio FOREIGN KEY (id_comercio) REFERENCES comercio(id_comercio)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
