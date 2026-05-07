-- DeTuBarrio: seed manual de referencia
--
-- Este archivo es una referencia limpia para cargar datos de ejemplo sobre una base
-- ya creada por las migraciones Flyway. No forma parte del arranque automático.
--
-- Orden recomendado:
-- 1. Ejecutar migraciones Flyway
-- 2. Ejecutar este seed si quieres datos de demo consistentes

SET NAMES utf8mb4;

-- =====================
-- Categorias
-- =====================
INSERT INTO categoria (id_categoria, nombre_categoria, descripcion) VALUES
    (1, 'Hostelería', 'Restaurantes, cafeterías y bares'),
    (2, 'Comercio', 'Tiendas y comercios minoristas')
ON DUPLICATE KEY UPDATE
    nombre_categoria = VALUES(nombre_categoria),
    descripcion = VALUES(descripcion);

-- =====================
-- Usuarios base
-- =====================
INSERT INTO usuario (id_usuario, nombre, email, password_hash, rol, id_comercio) VALUES
    (1, 'Ana García', 'ana@detubarrio.local', '$2a$10$7Qx1eE2y3Zq9h1Gf7kV/eOqKf1z9Yc8b0Lq1YvK6bS9uJ2c3d4eFG', 'ADMIN', NULL),
    (2, 'Pablo López', 'pablo@detubarrio.local', '$2a$10$7Qx1eE2y3Zq9h1Gf7kV/eOqKf1z9Yc8b0Lq1YvK6bS9uJ2c3d4eFG', 'USUARIO', NULL)
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    email = VALUES(email),
    password_hash = VALUES(password_hash),
    rol = VALUES(rol),
    id_comercio = VALUES(id_comercio);

-- =====================
-- Comercio publico visible
-- =====================
INSERT INTO comercio (
    id_comercio,
    nombre_comercio,
    descripcion,
    horario,
    dias_apertura,
    logo,
    banner,
    id_categoria,
    estado,
    gestion_autorizada,
    fecha_solicitud
) VALUES (
    1,
    'Café Central',
    'Cafetería con productos locales y terraza',
    '08:00-22:00',
    'Lunes-Domingo',
    '/images/logo_central.png',
    '/images/banner_central.jpg',
    1,
    'APROBADO',
    TRUE,
    CURRENT_TIMESTAMP
)
ON DUPLICATE KEY UPDATE
    nombre_comercio = VALUES(nombre_comercio),
    descripcion = VALUES(descripcion),
    horario = VALUES(horario),
    dias_apertura = VALUES(dias_apertura),
    logo = VALUES(logo),
    banner = VALUES(banner),
    id_categoria = VALUES(id_categoria),
    estado = VALUES(estado),
    gestion_autorizada = VALUES(gestion_autorizada),
    fecha_solicitud = VALUES(fecha_solicitud);

-- =====================
-- Estadisticas y contenido asociado
-- =====================
INSERT INTO estadisticas (id_estadistica, numero_visitas, puntuacion_media, total_ventas, id_comercio) VALUES
    (1, 1250, 4.5, 320, 1)
ON DUPLICATE KEY UPDATE
    numero_visitas = VALUES(numero_visitas),
    puntuacion_media = VALUES(puntuacion_media),
    total_ventas = VALUES(total_ventas),
    id_comercio = VALUES(id_comercio);

INSERT INTO producto (id_producto, nombre_producto, descripcion, imagen) VALUES
    (1, 'Café Espresso', 'Café espresso de tueste medio, servido en taza pequeña', '/images/cafe.png')
ON DUPLICATE KEY UPDATE
    nombre_producto = VALUES(nombre_producto),
    descripcion = VALUES(descripcion),
    imagen = VALUES(imagen);

INSERT INTO comercio_producto (id_comercio_producto, id_comercio, id_producto, stock, precio) VALUES
    (1, 1, 1, 120, 1.50)
ON DUPLICATE KEY UPDATE
    id_comercio = VALUES(id_comercio),
    id_producto = VALUES(id_producto),
    stock = VALUES(stock),
    precio = VALUES(precio);

INSERT INTO resena (
    id_resena,
    titulo,
    comentario,
    fecha,
    valoracion,
    autor_nombre,
    autor_email,
    id_cliente,
    id_comercio
) VALUES (
    1,
    'Excelente café',
    'Ambiente agradable y personal atento.',
    CURRENT_TIMESTAMP,
    5,
    'Pablo López',
    'pablo@detubarrio.local',
    NULL,
    1
)
ON DUPLICATE KEY UPDATE
    titulo = VALUES(titulo),
    comentario = VALUES(comentario),
    fecha = VALUES(fecha),
    valoracion = VALUES(valoracion),
    autor_nombre = VALUES(autor_nombre),
    autor_email = VALUES(autor_email),
    id_cliente = VALUES(id_cliente),
    id_comercio = VALUES(id_comercio);
