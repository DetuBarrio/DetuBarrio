-- ============================================================================
-- V2__seed_data.sql
-- Datos de semilla para DeTuBarrio - Entorno de producción
-- ============================================================================

-- Desactivamos FKs para poder limpiar y reinsertar datos
SET FOREIGN_KEY_CHECKS = 0;

-- password_reset_tokens es creada por Hibernate (ddl-auto=update) después de
-- Flyway, así que la dropeamos si existe para que Hibernate la reconstruya.
DROP TABLE IF EXISTS password_reset_tokens;

-- disponibilidades también es creada por Hibernate, pero necesitamos insertar
-- datos en ella, así que la creamos aquí con la misma estructura que espera Hibernate.
CREATE TABLE IF NOT EXISTS disponibilidades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comercio_id BIGINT,
    fecha DATE,
    hora_inicio TIME,
    hora_fin TIME,
    reservado BOOLEAN DEFAULT FALSE
);

-- ============================================================
-- 1. LIMPIEZA TOTAL DE DATOS EXISTENTES
-- ============================================================
DELETE FROM disponibilidades;
DELETE FROM reserva;
DELETE FROM resena;
DELETE FROM comercio_producto;
DELETE FROM cliente_favoritos_comercio;
DELETE FROM trabajador_comercio;
DELETE FROM estadisticas;
DELETE FROM contrato;
DELETE FROM trabajador;
DELETE FROM persona;
DELETE FROM compra_producto;
DELETE FROM compra;
DELETE FROM servicio;
DELETE FROM cliente;
DELETE FROM producto;
DELETE FROM comercio;
DELETE FROM usuario;
DELETE FROM mensaje_contacto;
DELETE FROM solicitud_colaboracion;
DELETE FROM categoria;

-- ============================================================
-- 2. CATEGORÍAS
-- ============================================================
INSERT INTO categoria (id_categoria, nombre_categoria, descripcion) VALUES
(1,  'Estética y Belleza',     'Servicios de peluquería, manicura, maquillaje y tratamientos de belleza'),
(2,  'Alimentación',           'Productos frescos, supermercados, tiendas de alimentación y delicatessen'),
(3,  'Salud y Bienestar',      'Centros médicos, fisioterapia, psicología y servicios de salud'),
(4,  'Moda y Complementos',    'Ropa, calzado, accesorios y complementos de moda'),
(5,  'Hogar y Decoración',     'Tiendas de decoración, muebles, menaje y artículos para el hogar'),
(6,  'Deportes y Ocio',        'Gimnasios, centros deportivos, actividades de ocio y tiempo libre'),
(7,  'Educación y Formación',  'Academias, clases particulares, formación y cursos'),
(8,  'Servicios Profesionales','Asesorías, gestorías, servicios legales y profesionales'),
(9,  'Restauración',           'Restaurantes, cafeterías, bares y establecimientos de comida'),
(10, 'Tecnología',             'Tiendas de informática, telefonía, electrónica y servicios tecnológicos');

-- ============================================================
-- 3. USUARIOS (contraseñas en Bcrypt)
--    admin@gmail.com / zeta123
-- ============================================================
INSERT INTO usuario (id_usuario, nombre, email, password_hash, rol, id_comercio) VALUES
(1,  'Admin DeTuBarrio',   'admin@gmail.com',             '$2b$10$aeWvy/HdZvu.A.6sG0oqTuFIA7XF.qm2DVwos9V438qwZ2W/EEcMa', 'ADMIN', NULL),

(2,  'María García',       'maria.garcia@gmail.com',       '$2b$10$cTkAL2p6Y9VyoyTcO2NxEOpDGicwoYOqbDYmm3B63gq4fy1aYJ75S', 'USUARIO', NULL),
(3,  'Carlos López',       'carlos.lopez@gmail.com',       '$2b$10$cTkAL2p6Y9VyoyTcO2NxEOpDGicwoYOqbDYmm3B63gq4fy1aYJ75S', 'USUARIO', NULL),
(4,  'Ana Martínez',       'ana.martinez@gmail.com',       '$2b$10$vgM6yowF2PvH9mcB9uruV.oPM1wspGBmzr13soPthaYVhZgoF4d2O', 'USUARIO', NULL),
(5,  'Javier Rodríguez',   'javier.rodriguez@gmail.com',   '$2b$10$vgM6yowF2PvH9mcB9uruV.oPM1wspGBmzr13soPthaYVhZgoF4d2O', 'USUARIO', NULL),
(6,  'Laura Sánchez',      'laura.sanchez@gmail.com',      '$2b$10$cTkAL2p6Y9VyoyTcO2NxEOpDGicwoYOqbDYmm3B63gq4fy1aYJ75S', 'USUARIO', NULL),
(7,  'David Fernández',    'david.fernandez@gmail.com',    '$2b$10$BM0TWLVOUF89BgMz0JWAleaCtiBX89FH13FIyHMIP2xgFjob.S9Q2', 'USUARIO', NULL),
(8,  'Elena González',     'elena.gonzalez@gmail.com',     '$2b$10$vgM6yowF2PvH9mcB9uruV.oPM1wspGBmzr13soPthaYVhZgoF4d2O', 'USUARIO', NULL),
(9,  'Miguel Ramírez',     'miguel.ramirez@gmail.com',     '$2b$10$BM0TWLVOUF89BgMz0JWAleaCtiBX89FH13FIyHMIP2xgFjob.S9Q2', 'USUARIO', NULL),

(10, 'Laura Díaz',          'contacto@belladivina.com',     '$2b$10$OIlEFY48lcVDD3P3T1/c5OsQXpvE23w7py56Yg.tsYDYOZIiYTueS', 'COMERCIO', NULL),
(11, 'Roberto Ruiz',        'info@peluqueriaestilo.com',   '$2b$10$OIlEFY48lcVDD3P3T1/c5OsQXpvE23w7py56Yg.tsYDYOZIiYTueS', 'COMERCIO', NULL),
(12, 'Pedro Hernández',     'info@mercadofresco.com',      '$2b$10$OIlEFY48lcVDD3P3T1/c5OsQXpvE23w7py56Yg.tsYDYOZIiYTueS', 'COMERCIO', NULL),
(13, 'Dra. Carmen Torres',  'citas@centromedicovital.com', '$2b$10$OIlEFY48lcVDD3P3T1/c5OsQXpvE23w7py56Yg.tsYDYOZIiYTueS', 'COMERCIO', NULL),
(14, 'Sofía Martín',        'hola@boutiquetrendy.com',     '$2b$10$OIlEFY48lcVDD3P3T1/c5OsQXpvE23w7py56Yg.tsYDYOZIiYTueS', 'COMERCIO', NULL),
(15, 'Alberto Sánchez',     'info@hogarconfort.com',       '$2b$10$OIlEFY48lcVDD3P3T1/c5OsQXpvE23w7py56Yg.tsYDYOZIiYTueS', 'COMERCIO', NULL),
(16, 'Marina Blanco',       'info@fitzone.com',            '$2b$10$OIlEFY48lcVDD3P3T1/c5OsQXpvE23w7py56Yg.tsYDYOZIiYTueS', 'COMERCIO', NULL),
(17, 'Jorge Pastor',        'avanza@academiaavanza.com',   '$2b$10$OIlEFY48lcVDD3P3T1/c5OsQXpvE23w7py56Yg.tsYDYOZIiYTueS', 'COMERCIO', NULL),
(18, 'Marta Gil',           'info@asesoriapro.com',        '$2b$10$OIlEFY48lcVDD3P3T1/c5OsQXpvE23w7py56Yg.tsYDYOZIiYTueS', 'COMERCIO', NULL),
(19, 'Antonio Ruiz',        'reservas@saborcasero.com',    '$2b$10$OIlEFY48lcVDD3P3T1/c5OsQXpvE23w7py56Yg.tsYDYOZIiYTueS', 'COMERCIO', NULL),
(20, 'Raquel Molina',       'central@cafeteriacentral.com','$2b$10$OIlEFY48lcVDD3P3T1/c5OsQXpvE23w7py56Yg.tsYDYOZIiYTueS', 'COMERCIO', NULL),
(21, 'Pablo Navarro',       'info@techzone.com',           '$2b$10$OIlEFY48lcVDD3P3T1/c5OsQXpvE23w7py56Yg.tsYDYOZIiYTueS', 'COMERCIO', NULL);

-- ============================================================
-- 4. COMERCIOS
-- ============================================================
INSERT INTO comercio (id_comercio, nombre_comercio, descripcion, horario, dias_apertura, logo, banner, estado, gestion_autorizada, fecha_solicitud, id_usuario_creador, id_categoria) VALUES
(1,  'Belleza Divina',
     'Centro de estética y belleza especializado en tratamientos faciales, corporales y peluquería.',
     '09:00 - 20:00', 'Lunes a Sábado',
     '/images/comercios/belleza-divina-logo.png', '/images/comercios/belleza-divina-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 120 DAY, 10, 1),

(2,  'Peluquería Estilo',
     'Peluquería unisex con las últimas tendencias en cortes, coloración y peinados.',
     '09:30 - 19:30', 'Lunes a Viernes',
     '/images/comercios/peluqueria-estilo-logo.png', '/images/comercios/peluqueria-estilo-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 100 DAY, 11, 1),

(3,  'Mercado Fresco',
     'Tienda de alimentación con productos frescos de proximidad.',
     '08:00 - 21:00', 'Lunes a Domingo',
     '/images/comercios/mercado-fresco-logo.png', '/images/comercios/mercado-fresco-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 90 DAY, 12, 2),

(4,  'Centro Médico Vital',
     'Centro médico multidisciplinar con consultas de medicina general, fisioterapia y psicología.',
     '08:00 - 21:00', 'Lunes a Viernes',
     '/images/comercios/centro-medico-logo.png', '/images/comercios/centro-medico-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 80 DAY, 13, 3),

(5,  'Boutique Trendy',
     'Tienda de moda femenina y masculina con las últimas tendencias.',
     '10:00 - 20:30', 'Lunes a Sábado',
     '/images/comercios/boutique-trendy-logo.png', '/images/comercios/boutique-trendy-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 70 DAY, 14, 4),

(6,  'Hogar & Confort',
     'Tienda de decoración y artículos para el hogar.',
     '10:00 - 19:00', 'Lunes a Sábado',
     '/images/comercios/hogar-confort-logo.png', '/images/comercios/hogar-confort-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 60 DAY, 15, 5),

(7,  'FitZone Centro Deportivo',
     'Gimnasio con sala de musculación, clases dirigidas, spinning y pilates.',
     '07:00 - 22:30', 'Lunes a Domingo',
     '/images/comercios/fitzone-logo.png', '/images/comercios/fitzone-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 50 DAY, 16, 6),

(8,  'Academia Avanza',
     'Academia de formación con cursos de idiomas, informática y apoyo escolar.',
     '09:00 - 21:00', 'Lunes a Viernes',
     '/images/comercios/academia-avanza-logo.png', '/images/comercios/academia-avanza-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 45 DAY, 17, 7),

(9,  'Asesoría Pro',
     'Servicios profesionales de asesoría fiscal, laboral y contable.',
     '09:00 - 18:00', 'Lunes a Viernes',
     '/images/comercios/asesoria-pro-logo.png', '/images/comercios/asesoria-pro-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 40 DAY, 18, 8),

(10, 'Sabor Casero',
     'Restaurante de cocina tradicional casera. Menú del día y raciones.',
     '13:00 - 16:00 / 20:00 - 23:00', 'Martes a Domingo',
     '/images/comercios/sabor-casero-logo.png', '/images/comercios/sabor-casero-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 35 DAY, 19, 9),

(11, 'Cafetería Central',
     'Cafetería acogedora con desayunos, meriendas y brunch.',
     '07:30 - 21:00', 'Lunes a Sábado',
     '/images/comercios/cafeteria-central-logo.png', '/images/comercios/cafeteria-central-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 30 DAY, 20, 9),

(12, 'TechZone',
     'Tienda de tecnología y electrónica. Venta y reparación.',
     '10:00 - 20:00', 'Lunes a Sábado',
     '/images/comercios/techzone-logo.png', '/images/comercios/techzone-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 25 DAY, 21, 10);

-- ============================================================
-- 5. VINCULAR COMERCIO CON SU USUARIO PROPIETARIO
-- ============================================================
UPDATE usuario SET id_comercio = 1 WHERE id_usuario = 10;
UPDATE usuario SET id_comercio = 2 WHERE id_usuario = 11;
UPDATE usuario SET id_comercio = 3 WHERE id_usuario = 12;
UPDATE usuario SET id_comercio = 4 WHERE id_usuario = 13;
UPDATE usuario SET id_comercio = 5 WHERE id_usuario = 14;
UPDATE usuario SET id_comercio = 6 WHERE id_usuario = 15;
UPDATE usuario SET id_comercio = 7 WHERE id_usuario = 16;
UPDATE usuario SET id_comercio = 8 WHERE id_usuario = 17;
UPDATE usuario SET id_comercio = 9 WHERE id_usuario = 18;
UPDATE usuario SET id_comercio = 10 WHERE id_usuario = 19;
UPDATE usuario SET id_comercio = 11 WHERE id_usuario = 20;
UPDATE usuario SET id_comercio = 12 WHERE id_usuario = 21;

-- ============================================================
-- 6. PERSONAS Y CLIENTES (para FKs de tablas huérfanas)
-- ============================================================
INSERT INTO persona (id_persona, nombre, apellidos, email, telefono) VALUES
(1, 'María',   'García López',    'maria.garcia@gmail.com',     '612345678'),
(2, 'Carlos',  'López Martínez',  'carlos.lopez@gmail.com',     '623456789'),
(3, 'Ana',     'Martínez Ruiz',   'ana.martinez@gmail.com',     '634567890'),
(4, 'Javier',  'Rodríguez Pérez', 'javier.rodriguez@gmail.com', '645678901'),
(5, 'Laura',   'Sánchez Gómez',   'laura.sanchez@gmail.com',    '656789012'),
(6, 'David',   'Fernández Díaz',  'david.fernandez@gmail.com',  '667890123'),
(7, 'Elena',   'González García', 'elena.gonzalez@gmail.com',   '678901234'),
(8, 'Miguel',  'Ramírez Torres',  'miguel.ramirez@gmail.com',   '689012345');

INSERT INTO cliente (id_cliente, estado, nivel, id_persona) VALUES
(1, TRUE, 'NORMAL', 1),
(2, TRUE, 'NORMAL', 2),
(3, TRUE, 'PRO',    3),
(4, TRUE, 'NORMAL', 4),
(5, TRUE, 'NORMAL', 5),
(6, TRUE, 'PRO',    6),
(7, TRUE, 'NORMAL', 7),
(8, TRUE, 'NORMAL', 8);

INSERT INTO servicio (id_servicio, nombre_servicio, descripcion, precio, id_cliente) VALUES
(1,  'RESERVA', 'Reserva Belleza Divina',   35.00, 1),
(2,  'RESERVA', 'Reserva Peluquería Estilo', 25.00, 2),
(3,  'RESERVA', 'Reserva Centro Médico',     50.00, 3),
(4,  'RESERVA', 'Reserva FitZone',           40.00, 4),
(5,  'RESERVA', 'Reserva Sabor Casero',      30.00, 5),
(6,  'RESERVA', 'Reserva Cafetería Central', 12.00, 6),
(7,  'RESERVA', 'Reserva Belleza Divina',    45.00, 7),
(8,  'RESERVA', 'Reserva Academia Avanza',   60.00, 8),
(9,  'RESERVA', 'Reserva Centro Médico',     65.00, 1),
(10, 'RESERVA', 'Reserva FitZone',           35.00, 2);

-- ============================================================
-- 7. DISPONIBILIDADES
-- ============================================================
INSERT INTO disponibilidades (id, comercio_id, fecha, hora_inicio, hora_fin, reservado) VALUES
(1,  1, CURDATE() + INTERVAL 1 DAY,  '09:00', '10:00', FALSE),
(2,  1, CURDATE() + INTERVAL 1 DAY,  '10:00', '11:00', FALSE),
(3,  1, CURDATE() + INTERVAL 1 DAY,  '11:00', '12:00', FALSE),
(4,  1, CURDATE() + INTERVAL 1 DAY,  '16:00', '17:00', FALSE),
(5,  1, CURDATE() + INTERVAL 2 DAY,  '09:00', '10:00', FALSE),
(6,  1, CURDATE() + INTERVAL 2 DAY,  '10:00', '11:00', FALSE),
(7,  2, CURDATE() + INTERVAL 1 DAY,  '09:30', '10:30', FALSE),
(8,  2, CURDATE() + INTERVAL 1 DAY,  '10:30', '11:30', FALSE),
(9,  2, CURDATE() + INTERVAL 1 DAY,  '11:30', '12:30', FALSE),
(10, 2, CURDATE() + INTERVAL 1 DAY,  '16:00', '17:00', FALSE),
(11, 2, CURDATE() + INTERVAL 2 DAY,  '09:30', '10:30', FALSE),
(12, 2, CURDATE() + INTERVAL 2 DAY,  '10:30', '11:30', FALSE),
(13, 4, CURDATE() + INTERVAL 1 DAY,  '08:00', '09:00', FALSE),
(14, 4, CURDATE() + INTERVAL 1 DAY,  '09:00', '10:00', FALSE),
(15, 4, CURDATE() + INTERVAL 1 DAY,  '10:00', '11:00', FALSE),
(16, 4, CURDATE() + INTERVAL 1 DAY,  '11:00', '12:00', FALSE),
(17, 4, CURDATE() + INTERVAL 1 DAY,  '12:00', '13:00', FALSE),
(18, 4, CURDATE() + INTERVAL 1 DAY,  '16:00', '17:00', FALSE),
(19, 4, CURDATE() + INTERVAL 2 DAY,  '09:00', '10:00', FALSE),
(20, 4, CURDATE() + INTERVAL 2 DAY,  '10:00', '11:00', FALSE),
(21, 7, CURDATE() + INTERVAL 1 DAY,  '07:00', '08:00', FALSE),
(22, 7, CURDATE() + INTERVAL 1 DAY,  '08:00', '09:00', FALSE),
(23, 7, CURDATE() + INTERVAL 1 DAY,  '09:00', '10:00', FALSE),
(24, 7, CURDATE() + INTERVAL 2 DAY,  '07:00', '08:00', FALSE),
(25, 7, CURDATE() + INTERVAL 2 DAY,  '08:00', '09:00', FALSE),
(26, 8, CURDATE() + INTERVAL 1 DAY,  '16:00', '17:00', FALSE),
(27, 8, CURDATE() + INTERVAL 1 DAY,  '17:00', '18:00', FALSE),
(28, 8, CURDATE() + INTERVAL 1 DAY,  '18:00', '19:00', FALSE),
(29, 8, CURDATE() + INTERVAL 2 DAY,  '16:00', '17:00', FALSE),
(30, 8, CURDATE() + INTERVAL 2 DAY,  '17:00', '18:00', FALSE),
(31, 10, CURDATE() + INTERVAL 1 DAY, '13:00', '14:00', FALSE),
(32, 10, CURDATE() + INTERVAL 1 DAY, '14:00', '15:00', FALSE),
(33, 10, CURDATE() + INTERVAL 1 DAY, '15:00', '16:00', FALSE),
(34, 10, CURDATE() + INTERVAL 1 DAY, '20:00', '21:00', FALSE),
(35, 10, CURDATE() + INTERVAL 2 DAY, '13:00', '14:00', FALSE),
(36, 10, CURDATE() + INTERVAL 2 DAY, '14:00', '15:00', FALSE);

-- ============================================================
-- 8. RESERVAS
-- ============================================================
INSERT INTO reserva (id_reserva, estado_reserva, id_servicio, id_comercio, id_usuario, id_disponibilidad, fecha_creacion) VALUES
(1, 'CONFIRMADA', 1, 1, 2, 1,  NOW() - INTERVAL 5 DAY),
(2, 'FINALIZADO', 2, 2, 3, 9,  NOW() - INTERVAL 10 DAY),
(3, 'CONFIRMADA', 3, 4, 4, 14, NOW() - INTERVAL 3 DAY),
(4, 'PROCESO',    4, 7, 5, 21, NOW() - INTERVAL 1 DAY),
(5, 'CONFIRMADA', 5, 10, 6, 31, NOW() - INTERVAL 2 DAY),
(6, 'PENDIENTE',  6, 11, 7, 33, NOW()),
(7, 'FINALIZADO', 7, 1, 8, 3,  NOW() - INTERVAL 15 DAY),
(8, 'CONFIRMADA', 8, 8, 9, 26, NOW() - INTERVAL 4 DAY),
(9, 'PENDIENTE',  9, 4, 2, 19, NOW()),
(10, 'FINALIZADO', 10, 7, 3, 22, NOW() - INTERVAL 20 DAY);

-- ============================================================
-- 9. PRODUCTOS
-- ============================================================
INSERT INTO producto (id_producto, nombre_producto, descripcion, imagen) VALUES
(1,  'Corte de pelo mujer',     'Corte personalizado con lavado y peinado',              '/images/productos/corte-mujer.jpg'),
(2,  'Corte de pelo hombre',    'Corte y arreglo de barba incluido',                    '/images/productos/corte-hombre.jpg'),
(3,  'Manicura completa',       'Manicura con esmaltado semipermanente',                 '/images/productos/manicura.jpg'),
(4,  'Pedicura spa',            'Pedicura con exfoliación y masaje',                     '/images/productos/pedicura.jpg'),
(5,  'Consulta medicina general','Revisión médica completa',                              '/images/productos/consulta-medica.jpg'),
(6,  'Sesión de fisioterapia',  'Sesión de 50 minutos con diagnóstico personalizado',   '/images/productos/fisioterapia.jpg'),
(7,  'Clase de yoga',           'Clase grupal de yoga de 60 minutos',                    '/images/productos/yoga.jpg'),
(8,  'Entrenamiento personal',  'Sesión individual con entrenador certificado',          '/images/productos/entrenamiento.jpg'),
(9,  'Curso de inglés B1',      'Curso intensivo de inglés nivel intermedio',            '/images/productos/curso-ingles.jpg'),
(10, 'Clase de matemáticas',    'Apoyo escolar personalizado de matemáticas',            '/images/productos/clase-mates.jpg'),
(11, 'Asesoría fiscal',         'Declaración de la renta y asesoramiento fiscal',        '/images/productos/asesoria-fiscal.jpg'),
(12, 'Gestión de nóminas',      'Elaboración y gestión de nóminas y seguros sociales',  '/images/productos/nominas.jpg'),
(13, 'Menú del día',            'Primer plato, segundo plato, postre y bebida',          '/images/productos/menu-dia.jpg'),
(14, 'Café con leche',          'Café de especialidad con leche de la casa',            '/images/productos/cafe.jpg'),
(15, 'Brunch completo',         'Brunch con café, zumo, tostada y bollería',             '/images/productos/brunch.jpg'),
(16, 'Reparación de móvil',     'Diagnóstico y reparación de pantalla y componentes',   '/images/productos/reparacion-movil.jpg'),
(17, 'Limpieza de ordenador',   'Limpieza interna y externa + cambio de pasta térmica',  '/images/productos/limpieza-pc.jpg'),
(18, 'Cesta ecológica',         'Fruta y verdura ecológica de temporada',                 '/images/productos/cesta-ecologica.jpg'),
(19, 'Decoración hogar',        'Artículo de decoración para el hogar',                  '/images/productos/decoracion.jpg'),
(20, 'Prenda de moda',          'Prenda de ropa de última temporada',                    '/images/productos/prenda-moda.jpg');

-- ============================================================
-- 10. COMERCIO_PRODUCTO
-- ============================================================
INSERT INTO comercio_producto (id_comercio_producto, id_comercio, id_producto, stock, precio) VALUES
(1,  1,  1,  999, 35.00),
(2,  1,  3,  999, 28.00),
(3,  1,  4,  999, 32.00),
(4,  2,  1,  999, 25.00),
(5,  2,  2,  999, 18.00),
(6,  2,  3,  999, 22.00),
(7,  3,  18, 50,  15.00),
(8,  4,  5,  999, 50.00),
(9,  4,  6,  999, 35.00),
(10, 5,  20, 20,  45.00),
(11, 6,  19, 15,  25.00),
(12, 7,  7,  999, 12.00),
(13, 7,  8,  999, 30.00),
(14, 8,  9,  999, 120.00),
(15, 8,  10, 999, 25.00),
(16, 9,  11, 999, 80.00),
(17, 9,  12, 999, 100.00),
(18, 10, 13, 999, 14.50),
(19, 11, 14, 999, 2.50),
(20, 11, 15, 999, 12.00),
(21, 12, 16, 999, 65.00),
(22, 12, 17, 999, 35.00);

-- ============================================================
-- 11. RESEÑAS
-- ============================================================
INSERT INTO resena (id_resena, titulo, comentario, fecha, valoracion, autor_nombre, autor_email, id_comercio) VALUES
(1,  'Excelente servicio',      'Me encantó el trato recibido.',                          NOW() - INTERVAL 15 DAY, 5, 'María García',     'maria.garcia@gmail.com',     1),
(2,  'Muy profesionales',       'Gran atención al cliente.',                             NOW() - INTERVAL 12 DAY, 4, 'Carlos López',    'carlos.lopez@gmail.com',     2),
(3,  'Productos de calidad',    'La fruta y verdura son fresquísimas.',                   NOW() - INTERVAL 10 DAY, 5, 'Ana Martínez',    'ana.martinez@gmail.com',     3),
(4,  'Atención excelente',      'La doctora fue muy atenta y resolvió mis dudas.',        NOW() - INTERVAL 8 DAY,  5, 'Javier Rodríguez', 'javier.rodriguez@gmail.com', 4),
(5,  'Buena relación calidad-precio', 'Ropa moderna a precios asequibles.',               NOW() - INTERVAL 7 DAY,  4, 'Laura Sánchez',   'laura.sanchez@gmail.com',    5),
(6,  'Decoración preciosa',     'Encontré justo lo que buscaba para mi salón.',           NOW() - INTERVAL 6 DAY,  4, 'David Fernández', 'david.fernandez@gmail.com',  6),
(7,  'El mejor gimnasio',       'Instalaciones impecables y monitores profesionales.',    NOW() - INTERVAL 5 DAY,  5, 'Elena González',  'elena.gonzalez@gmail.com',   7),
(8,  'Clases muy útiles',       'El curso de inglés me ha ayudado muchísimo.',            NOW() - INTERVAL 4 DAY,  4, 'Miguel Ramírez',  'miguel.ramirez@gmail.com',   8),
(9,  'Gestión impecable',       'Me resolvieron toda la papeleta de la declaración.',     NOW() - INTERVAL 3 DAY,  5, 'María García',    'maria.garcia@gmail.com',     9),
(10, 'Comida casera deliciosa', 'El menú del día está espectacular.',                     NOW() - INTERVAL 2 DAY, 5, 'Carlos López',    'carlos.lopez@gmail.com',     10),
(11, 'Café de especialidad',    'El mejor café del barrio.',                              NOW() - INTERVAL 1 DAY, 4, 'Ana Martínez',    'ana.martinez@gmail.com',     11),
(12, 'Arreglaron mi móvil',     'Rápidos y eficientes.',                                  NOW(),                    4, 'Javier Rodríguez', 'javier.rodriguez@gmail.com', 12),
(13, 'Corte perfecto',          'Salí encantada con mi nuevo corte.',                     NOW() - INTERVAL 9 DAY, 5, 'Laura Sánchez',   'laura.sanchez@gmail.com',    1),
(14, 'Gran variedad',           'Tienen mucha variedad de productos ecológicos.',         NOW() - INTERVAL 6 DAY, 4, 'Elena González',  'elena.gonzalez@gmail.com',   3),
(15, 'Fisioterapia de 10',      'Me recuperé de la lesión en tiempo récord.',             NOW() - INTERVAL 4 DAY, 5, 'Miguel Ramírez',  'miguel.ramirez@gmail.com',   4),
(16, 'Muy recomendable',        'Trato cercano y profesional.',                           NOW() - INTERVAL 3 DAY, 4, 'David Fernández', 'david.fernandez@gmail.com',  8);

-- ============================================================
-- 12. FAVORITOS
-- ============================================================
INSERT INTO cliente_favoritos_comercio (id_cliente, id_comercio) VALUES
(1, 1), (1, 3), (1, 10),
(2, 2), (2, 4), (2, 7),
(3, 1), (3, 5), (3, 11),
(4, 3), (4, 8), (4, 12),
(5, 6), (5, 10),
(6, 7), (6, 4), (6, 9),
(7, 1), (7, 2), (7, 11),
(8, 8), (8, 12);

-- ============================================================
-- 13. ESTADÍSTICAS
-- ============================================================
INSERT INTO estadisticas (id_estadistica, numero_visitas, puntuacion_media, total_ventas, id_comercio) VALUES
(1,  1240, 4.8, 156, 1),
(2,  980,  4.5, 112, 2),
(3,  2100, 4.7, 340, 3),
(4,  850,  4.9, 98,  4),
(5,  670,  4.3, 76,  5),
(6,  430,  4.4, 52,  6),
(7,  3100, 4.6, 420, 7),
(8,  560,  4.5, 65,  8),
(9,  390,  4.8, 48,  9),
(10, 1850, 4.9, 280, 10),
(11, 2200, 4.7, 310, 11),
(12, 780,  4.4, 89,  12);

-- ============================================================
-- 14. SOLICITUDES DE COLABORACIÓN
-- ============================================================
INSERT INTO solicitud_colaboracion (id_solicitud_colaboracion, nombre_comercio, nombre_titular, email_comercio, telefono_comercio, categoria, descripcion, estado, terminos_aceptados, fecha_creacion) VALUES
(1, 'Panadería El Trigal',      'Manuel Pérez',     'info@panaderiaeltrigal.com',  '612111222', 'Alimentación', 'Panadería artesana con horno de leña.',                                           'APROBADA', TRUE, NOW() - INTERVAL 60 DAY),
(2, 'Lavandería EcoClean',      'Sara Molina',      'info@ecoclean.com',           '622333444', 'Hogar',        'Lavandería ecológica con recogida a domicilio.',                                 'APROBADA', TRUE, NOW() - INTERVAL 45 DAY),
(3, 'Clínica Dental Care',      'Dr. Antonio Ruiz', 'citas@clinicadentalcare.com', '632555666', 'Salud',        'Clínica dental con los últimos tratamientos.',                                    'PENDIENTE', TRUE, NOW() - INTERVAL 10 DAY),
(4, 'Mascotas Patas Felices',   'Lucía Hernández',  'info@patasfelices.com',       '642777888', 'Otros',        'Tienda de alimentación y accesorios para mascotas.',                             'RECHAZADA', TRUE, NOW() - INTERVAL 20 DAY);

-- ============================================================
-- 15. MENSAJES DE CONTACTO
-- ============================================================
INSERT INTO mensaje_contacto (id_mensaje_contacto, nombre, email, asunto, tipo, mensaje, fecha_creacion) VALUES
(1, 'Pedro Gómez',     'pedro.gomez@email.com',    'Quiero registrar mi comercio', 'registro',     'Tengo una frutería y me gustaría darme de alta en la plataforma.',              NOW() - INTERVAL 30 DAY),
(2, 'Rosa Martínez',   'rosa.martinez@email.com',  'Sugerencia de mejora',         'sugerencia',   'Sería genial si añadís un mapa para ver los comercios cercanos.',               NOW() - INTERVAL 20 DAY),
(3, 'Jorge Fernández', 'jorge.fernandez@email.com','Problema con una reserva',      'problema',     'Hice una reserva y no he recibido confirmación.',                               NOW() - INTERVAL 10 DAY),
(4, 'Ana Belén García','anabelen@email.com',       'Colaboración con el proyecto', 'colaboracion', 'Soy dueña de un gimnasio y me encantaría formar parte de DeTuBarrio.',          NOW() - INTERVAL 5 DAY),
(5, 'Carlos Ruiz',     'carlos.ruiz@email.com',    'Consulta sobre facturación',   'consulta',     'Necesito una factura de mi última compra en el Mercado Fresco.',                NOW() - INTERVAL 2 DAY);

-- ============================================================
-- REACTIVAMOS FKs
-- ============================================================
SET FOREIGN_KEY_CHECKS = 1;
