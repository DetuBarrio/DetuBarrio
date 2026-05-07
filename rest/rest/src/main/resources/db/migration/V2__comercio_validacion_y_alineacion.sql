-- V2: validacion de comercios + alineacion de esquema con entidades JPA

-- =====================
-- 1) Integracion profesional del script de validacion de comercios
-- =====================

-- estado
SET @c := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'comercio' AND COLUMN_NAME = 'estado'
);
SET @sql := IF(@c = 0,
    'ALTER TABLE comercio ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT ''PENDIENTE''',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- fecha_solicitud
SET @c := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'comercio' AND COLUMN_NAME = 'fecha_solicitud'
);
SET @sql := IF(@c = 0,
    'ALTER TABLE comercio ADD COLUMN fecha_solicitud DATETIME NULL',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- motivo_rechazo
SET @c := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'comercio' AND COLUMN_NAME = 'motivo_rechazo'
);
SET @sql := IF(@c = 0,
    'ALTER TABLE comercio ADD COLUMN motivo_rechazo VARCHAR(500)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- id_usuario_creador
SET @c := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'comercio' AND COLUMN_NAME = 'id_usuario_creador'
);
SET @sql := IF(@c = 0,
    'ALTER TABLE comercio ADD COLUMN id_usuario_creador BIGINT',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- FK usuario creador
SET @fk := (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'comercio'
      AND CONSTRAINT_NAME = 'fk_comercio_usuario_creador'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
    'ALTER TABLE comercio ADD CONSTRAINT fk_comercio_usuario_creador FOREIGN KEY (id_usuario_creador) REFERENCES usuario(id_usuario)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- indice por estado
SET @idx := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'comercio'
      AND INDEX_NAME = 'idx_comercio_estado'
);
SET @sql := IF(@idx = 0,
    'CREATE INDEX idx_comercio_estado ON comercio(estado)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- backfill de fecha_solicitud
UPDATE comercio
SET fecha_solicitud = COALESCE(fecha_solicitud, CURRENT_TIMESTAMP)
WHERE fecha_solicitud IS NULL;

-- regla de estado para registros historicos
UPDATE comercio
SET estado = 'APROBADO'
WHERE estado = 'PENDIENTE' AND id_usuario_creador IS NULL;

-- =====================
-- 2) Columnas adicionales requeridas por entidades actuales
-- =====================

-- comercio.gestion_autorizada
SET @c := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'comercio' AND COLUMN_NAME = 'gestion_autorizada'
);
SET @sql := IF(@c = 0,
    'ALTER TABLE comercio ADD COLUMN gestion_autorizada BOOLEAN NOT NULL DEFAULT FALSE',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- comercio.motivo_bloqueo_gestion
SET @c := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'comercio' AND COLUMN_NAME = 'motivo_bloqueo_gestion'
);
SET @sql := IF(@c = 0,
    'ALTER TABLE comercio ADD COLUMN motivo_bloqueo_gestion VARCHAR(500)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- producto.imagen
SET @c := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'producto' AND COLUMN_NAME = 'imagen'
);
SET @sql := IF(@c = 0,
    'ALTER TABLE producto ADD COLUMN imagen VARCHAR(255)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =====================
-- 3) Tablas faltantes del dominio actual
-- =====================

CREATE TABLE IF NOT EXISTS mensaje_contacto (
    id_mensaje_contacto BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    asunto VARCHAR(120) NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    mensaje VARCHAR(2000) NOT NULL,
    fecha_creacion DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS solicitud_colaboracion (
    id_solicitud_colaboracion BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_comercio VARCHAR(120) NOT NULL,
    nombre_titular VARCHAR(120) NOT NULL,
    email_comercio VARCHAR(150) NOT NULL,
    telefono_comercio VARCHAR(30) NOT NULL,
    categoria VARCHAR(80) NOT NULL,
    descripcion VARCHAR(3000),
    id_comercio_origen BIGINT,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    motivo_rechazo VARCHAR(500),
    fecha_resolucion DATETIME,
    terminos_aceptados BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion DATETIME NOT NULL
);
