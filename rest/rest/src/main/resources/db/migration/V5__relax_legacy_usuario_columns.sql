-- Compatibilidad con modelo Usuario actual (sin username/id_persona obligatorios)
ALTER TABLE usuario
    MODIFY COLUMN username VARCHAR(100) NULL;

ALTER TABLE usuario
    MODIFY COLUMN id_persona BIGINT NULL;
