-- Ajuste de tipo para compatibilidad con entidad JPA (Integer)
ALTER TABLE resena
    MODIFY COLUMN valoracion INT NOT NULL;
