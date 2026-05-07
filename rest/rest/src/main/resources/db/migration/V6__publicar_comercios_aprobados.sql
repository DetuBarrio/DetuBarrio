-- Los comercios aprobados deben ser visibles en el listado publico del frontend.
-- Esto mantiene el comportamiento esperado sin abrir comercios pendientes por defecto.

UPDATE comercio
SET gestion_autorizada = TRUE
WHERE estado = 'APROBADO'
  AND gestion_autorizada = FALSE;
