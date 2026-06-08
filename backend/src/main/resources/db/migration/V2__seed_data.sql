-- ============================================================================
-- V2__seed_data.sql
-- Datos de semilla para DeTuBarrio - Entorno de producción
-- Contraseñas hasheadas con bcrypt
-- ============================================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS password_reset_tokens;

CREATE TABLE IF NOT EXISTS disponibilidades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comercio_id BIGINT,
    fecha DATE,
    hora_inicio TIME,
    hora_fin TIME,
    reservado BOOLEAN DEFAULT FALSE
);

-- ============================================================
-- 1. LIMPIEZA TOTAL
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
-- 2. CATEGORÍAS (7)
-- ============================================================
INSERT INTO categoria (id_categoria, nombre_categoria, descripcion) VALUES
(1, 'Estética y Belleza',   'Peluquería, manicura, maquillaje, spas y tratamientos de belleza'),
(2, 'Alimentación',          'Productos frescos, supermercados, fruterías, carnicerías y delicatessen'),
(3, 'Salud y Bienestar',     'Centros médicos, fisioterapia, psicología, clínicas dentales y ópticas'),
(4, 'Deportes y Ocio',       'Gimnasios, centros deportivos, pilates, yoga y actividades de tiempo libre'),
(5, 'Educación y Formación', 'Academias, clases particulares, idiomas, música y formación profesional'),
(6, 'Restauración',          'Restaurantes, cafeterías, bares, pizzerías y establecimientos de comida'),
(7, 'Tecnología',            'Informática, telefonía, electrónica, reparaciones y servicios tecnológicos');

-- ============================================================
-- 3. USUARIOS
-- ============================================================
INSERT INTO usuario (id_usuario, nombre, email, password_hash, rol, id_comercio) VALUES
(1,  'Admin DeTuBarrio',    'admin@gmail.com',             '$2b$10$eBn2JdPbExEhtNtQdePfGepfBgdUAVpxm0yTPRX2aEyC5LTFraHby', 'ADMIN', NULL),

(2,  'María García',        'maria.garcia@gmail.com',       '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'USUARIO', NULL),
(3,  'Carlos López',        'carlos.lopez@gmail.com',       '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'USUARIO', NULL),
(4,  'Ana Martínez',        'ana.martinez@gmail.com',       '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'USUARIO', NULL),
(5,  'Javier Rodríguez',    'javier.rodriguez@gmail.com',   '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'USUARIO', NULL),
(6,  'Laura Sánchez',       'laura.sanchez@gmail.com',      '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'USUARIO', NULL),
(7,  'David Fernández',     'david.fernandez@gmail.com',    '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'USUARIO', NULL),
(8,  'Elena González',      'elena.gonzalez@gmail.com',     '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'USUARIO', NULL),
(9,  'Miguel Ramírez',      'miguel.ramirez@gmail.com',     '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'USUARIO', NULL),

-- Dueños de comercios APROBADOS
(10, 'Laura Díaz',          'contacto@belladivina.com',     '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(11, 'Roberto Ruiz',        'info@peluqueriaestilo.com',   '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(12, 'Pedro Hernández',     'info@mercadofresco.com',      '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(13, 'Dra. Carmen Torres',  'citas@centromedicovital.com', '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(14, 'Marina Blanco',       'info@fitzone.com',            '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(15, 'Jorge Pastor',        'avanza@academiaavanza.com',   '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(16, 'Antonio Ruiz',        'reservas@saborcasero.com',    '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(17, 'Raquel Molina',       'central@cafeteriacentral.com','$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(18, 'Pablo Navarro',       'info@techzone.com',           '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),

-- Nuevos dueños APROBADOS
(19, 'Elena Serrano',       'info@nuevoamanecer.com',      '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(20, 'Juan Vega',           'hola@fruteriavega.com',       '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(21, 'Manuel Carmona',      'info@carniceriaslecta.com',   '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(22, 'Dr. Antonio Ruiz',    'citas@clinicadentalcare.com', '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(23, 'Sara Montero',        'info@fisiomove.com',          '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(24, 'Daniel Castro',       'info@crossfitbox.com',        '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(25, 'Laura Moreno',        'info@escuelamusica.com',      '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(26, 'Mario Rossi',         'reservas@pizzeriaroma.com',   '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(27, 'Álvaro Mesa',         'info@pcexpress.com',          '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(28, 'Sara Molina',         'info@ecoclean.com',           '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(29, 'Lucía Hernández',     'info@patasfelices.com',       '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL),
(30, 'Manuel Pérez',        'info@panaderiaeltrigal.com',  '$2b$10$kEeGIg2cWn/eNSgTUu4k1Oh3xXhtuLlN9zRM6eQMvRiaE2sjZeW4q', 'COMERCIO', NULL);

-- ============================================================
-- 4. COMERCIOS APROBADOS (18)
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
     'Supermercado de barrio con productos frescos, fruta, verdura y alimentación en general.',
     '08:00 - 21:00', 'Lunes a Domingo',
     '/images/comercios/mercado-fresco-logo.png', '/images/comercios/mercado-fresco-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 90 DAY, 12, 2),

(4,  'Centro Médico Vital',
     'Centro médico multidisciplinar con consultas de medicina general, fisioterapia y psicología.',
     '08:00 - 21:00', 'Lunes a Viernes',
     '/images/comercios/centro-medico-logo.png', '/images/comercios/centro-medico-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 80 DAY, 13, 3),

(5,  'FitZone Centro Deportivo',
     'Gimnasio con sala de musculación, clases dirigidas, spinning, yoga y pilates.',
     '07:00 - 22:30', 'Lunes a Domingo',
     '/images/comercios/fitzone-logo.png', '/images/comercios/fitzone-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 70 DAY, 14, 4),

(6,  'Academia Avanza',
     'Academia de formación con cursos de idiomas, informática, apoyo escolar y preparación de oposiciones.',
     '09:00 - 21:00', 'Lunes a Viernes',
     '/images/comercios/academia-avanza-logo.png', '/images/comercios/academia-avanza-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 60 DAY, 15, 5),

(7,  'Sabor Casero',
     'Restaurante de cocina tradicional casera. Menú del día, raciones y platos para llevar.',
     '13:00 - 16:00 / 20:00 - 23:00', 'Martes a Domingo',
     '/images/comercios/sabor-casero-logo.png', '/images/comercios/sabor-casero-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 55 DAY, 16, 6),

(8,  'Cafetería Central',
     'Cafetería acogedora con desayunos, meriendas, brunch y café de especialidad.',
     '07:30 - 21:00', 'Lunes a Sábado',
     '/images/comercios/cafeteria-central-logo.png', '/images/comercios/cafeteria-central-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 50 DAY, 17, 6),

(9,  'TechZone',
     'Tienda de tecnología y electrónica. Venta de equipos, accesorios y servicio de reparación.',
     '10:00 - 20:00', 'Lunes a Sábado',
     '/images/comercios/techzone-logo.png', '/images/comercios/techzone-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 45 DAY, 18, 7),

(10, 'Nuevo Amanecer',
     'Spá urbano con tratamientos de belleza, masajes, baños de vapor y rituales de bienestar.',
     '10:00 - 21:00', 'Lunes a Sábado',
     '/images/comercios/nuevo-amanecer-logo.png', '/images/comercios/nuevo-amanecer-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 40 DAY, 19, 1),

(11, 'Frutería La Vega',
     'Frutería y verdulería con productos ecológicos, de temporada y proximidad.',
     '08:30 - 20:30', 'Lunes a Sábado',
     '/images/comercios/fruteria-vega-logo.png', '/images/comercios/fruteria-vega-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 35 DAY, 20, 2),

(12, 'Carnicería Selecta',
     'Carnicería tradicional con carnes de primera calidad, embutidos y productos ibéricos.',
     '08:00 - 20:00', 'Lunes a Sábado',
     '/images/comercios/carniceria-selecta-logo.png', '/images/comercios/carniceria-selecta-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 30 DAY, 21, 2),

(13, 'Clínica Dental Care',
     'Clínica dental con los últimos tratamientos: limpieza, blanqueamiento, ortodoncia e implantes.',
     '09:00 - 20:00', 'Lunes a Viernes',
     '/images/comercios/dental-care-logo.png', '/images/comercios/dental-care-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 25 DAY, 22, 3),

(14, 'Fisioterapia Move',
     'Centro de fisioterapia y rehabilitación especializado en lesiones deportivas y crónicas.',
     '09:00 - 20:00', 'Lunes a Viernes',
     '/images/comercios/fisio-move-logo.png', '/images/comercios/fisio-move-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 20 DAY, 23, 3),

(15, 'CrossFit Box',
     'Box de CrossFit con entrenamiento funcional, HIIT y preparación física personalizada.',
     '07:00 - 22:00', 'Lunes a Viernes',
     '/images/comercios/crossfit-box-logo.png', '/images/comercios/crossfit-box-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 18 DAY, 24, 4),

(16, 'Escuela de Música',
     'Escuela de música con clases de piano, guitarra, canto, violín y lenguaje musical.',
     '15:00 - 21:00', 'Lunes a Viernes',
     '/images/comercios/escuela-musica-logo.png', '/images/comercios/escuela-musica-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 15 DAY, 25, 5),

(17, 'Pizzería Roma',
     'Pizzería artesanal con horno de leña, pastas caseras y las mejores pizzas del barrio.',
     '12:00 - 16:00 / 20:00 - 23:30', 'Martes a Domingo',
     '/images/comercios/pizzeria-roma-logo.png', '/images/comercios/pizzeria-roma-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 12 DAY, 26, 6),

(18, 'PC Express',
     'Tienda de informática: venta de ordenadores, componentes, periféricos y servicio técnico.',
     '10:00 - 20:00', 'Lunes a Viernes',
     '/images/comercios/pc-express-logo.png', '/images/comercios/pc-express-banner.png',
     'APROBADO', TRUE, NOW() - INTERVAL 10 DAY, 27, 7);

-- ============================================================
-- 4b. COMERCIOS PENDIENTES DE APROBACIÓN (solicitudes pendientes)
-- ============================================================
INSERT INTO comercio (id_comercio, nombre_comercio, descripcion, horario, dias_apertura, logo, banner, estado, gestion_autorizada, fecha_solicitud, id_usuario_creador, id_categoria) VALUES
(19, 'Lavandería EcoClean',
     'Lavandería ecológica con servicios de limpieza en seco, lavado a domicilio y planchado.',
     '09:00 - 19:00', 'Lunes a Viernes',
     '/images/comercios/ecoclean-logo.png', '/images/comercios/ecoclean-banner.png',
     'PENDIENTE', FALSE, NOW() - INTERVAL 5 DAY, 28, 7),

(20, 'Mascotas Patas Felices',
     'Tienda de alimentación, accesorios y servicios de peluquería canina y felina.',
     '10:00 - 20:00', 'Lunes a Sábado',
     '/images/comercios/patas-felices-logo.png', '/images/comercios/patas-felices-banner.png',
     'PENDIENTE', FALSE, NOW() - INTERVAL 3 DAY, 29, 2),

(21, 'Panadería El Trigal',
     'Panadería artesana con horno de leña, masa madre, bollería y repostería tradicional.',
     '07:00 - 20:00', 'Martes a Domingo',
     '/images/comercios/panaderia-trigal-logo.png', '/images/comercios/panaderia-trigal-banner.png',
     'PENDIENTE', FALSE, NOW() - INTERVAL 7 DAY, 30, 2);

-- ============================================================
-- 5. VINCULAR COMERCIO CON SU USUARIO PROPIETARIO
-- ============================================================
UPDATE usuario SET id_comercio = 1  WHERE id_usuario = 10;
UPDATE usuario SET id_comercio = 2  WHERE id_usuario = 11;
UPDATE usuario SET id_comercio = 3  WHERE id_usuario = 12;
UPDATE usuario SET id_comercio = 4  WHERE id_usuario = 13;
UPDATE usuario SET id_comercio = 5  WHERE id_usuario = 14;
UPDATE usuario SET id_comercio = 6  WHERE id_usuario = 15;
UPDATE usuario SET id_comercio = 7  WHERE id_usuario = 16;
UPDATE usuario SET id_comercio = 8  WHERE id_usuario = 17;
UPDATE usuario SET id_comercio = 9  WHERE id_usuario = 18;
UPDATE usuario SET id_comercio = 10 WHERE id_usuario = 19;
UPDATE usuario SET id_comercio = 11 WHERE id_usuario = 20;
UPDATE usuario SET id_comercio = 12 WHERE id_usuario = 21;
UPDATE usuario SET id_comercio = 13 WHERE id_usuario = 22;
UPDATE usuario SET id_comercio = 14 WHERE id_usuario = 23;
UPDATE usuario SET id_comercio = 15 WHERE id_usuario = 24;
UPDATE usuario SET id_comercio = 16 WHERE id_usuario = 25;
UPDATE usuario SET id_comercio = 17 WHERE id_usuario = 26;
UPDATE usuario SET id_comercio = 18 WHERE id_usuario = 27;

-- ============================================================
-- 6. PERSONAS Y CLIENTES
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
(10, 'RESERVA', 'Reserva FitZone',           35.00, 2),
(11, 'RESERVA', 'Reserva Nuevo Amanecer',    50.00, 3),
(12, 'RESERVA', 'Reserva Clínica Dental',    40.00, 4),
(13, 'RESERVA', 'Reserva Pizzería Roma',     25.00, 5),
(14, 'RESERVA', 'Reserva CrossFit Box',      30.00, 6);

-- ============================================================
-- 7. DISPONIBILIDADES
-- ============================================================
INSERT INTO disponibilidades (id, comercio_id, fecha, hora_inicio, hora_fin, reservado) VALUES
(1,  1,  CURDATE() + INTERVAL 1 DAY, '09:00', '10:00', FALSE),
(2,  1,  CURDATE() + INTERVAL 1 DAY, '10:00', '11:00', FALSE),
(3,  1,  CURDATE() + INTERVAL 1 DAY, '11:00', '12:00', FALSE),
(4,  1,  CURDATE() + INTERVAL 1 DAY, '16:00', '17:00', FALSE),
(5,  1,  CURDATE() + INTERVAL 2 DAY, '09:00', '10:00', FALSE),
(6,  1,  CURDATE() + INTERVAL 2 DAY, '10:00', '11:00', FALSE),
(7,  2,  CURDATE() + INTERVAL 1 DAY, '09:30', '10:30', FALSE),
(8,  2,  CURDATE() + INTERVAL 1 DAY, '10:30', '11:30', FALSE),
(9,  2,  CURDATE() + INTERVAL 1 DAY, '11:30', '12:30', FALSE),
(10, 2,  CURDATE() + INTERVAL 1 DAY, '16:00', '17:00', FALSE),
(11, 2,  CURDATE() + INTERVAL 2 DAY, '09:30', '10:30', FALSE),
(12, 2,  CURDATE() + INTERVAL 2 DAY, '10:30', '11:30', FALSE),
(13, 4,  CURDATE() + INTERVAL 1 DAY, '08:00', '09:00', FALSE),
(14, 4,  CURDATE() + INTERVAL 1 DAY, '09:00', '10:00', FALSE),
(15, 4,  CURDATE() + INTERVAL 1 DAY, '10:00', '11:00', FALSE),
(16, 4,  CURDATE() + INTERVAL 1 DAY, '11:00', '12:00', FALSE),
(17, 4,  CURDATE() + INTERVAL 1 DAY, '12:00', '13:00', FALSE),
(18, 4,  CURDATE() + INTERVAL 1 DAY, '16:00', '17:00', FALSE),
(19, 4,  CURDATE() + INTERVAL 2 DAY, '09:00', '10:00', FALSE),
(20, 4,  CURDATE() + INTERVAL 2 DAY, '10:00', '11:00', FALSE),
(21, 5,  CURDATE() + INTERVAL 1 DAY, '07:00', '08:00', FALSE),
(22, 5,  CURDATE() + INTERVAL 1 DAY, '08:00', '09:00', FALSE),
(23, 5,  CURDATE() + INTERVAL 1 DAY, '09:00', '10:00', FALSE),
(24, 5,  CURDATE() + INTERVAL 2 DAY, '07:00', '08:00', FALSE),
(25, 5,  CURDATE() + INTERVAL 2 DAY, '08:00', '09:00', FALSE),
(26, 6,  CURDATE() + INTERVAL 1 DAY, '16:00', '17:00', FALSE),
(27, 6,  CURDATE() + INTERVAL 1 DAY, '17:00', '18:00', FALSE),
(28, 6,  CURDATE() + INTERVAL 1 DAY, '18:00', '19:00', FALSE),
(29, 6,  CURDATE() + INTERVAL 2 DAY, '16:00', '17:00', FALSE),
(30, 6,  CURDATE() + INTERVAL 2 DAY, '17:00', '18:00', FALSE),
(31, 7,  CURDATE() + INTERVAL 1 DAY, '13:00', '14:00', FALSE),
(32, 7,  CURDATE() + INTERVAL 1 DAY, '14:00', '15:00', FALSE),
(33, 7,  CURDATE() + INTERVAL 1 DAY, '15:00', '16:00', FALSE),
(34, 7,  CURDATE() + INTERVAL 1 DAY, '20:00', '21:00', FALSE),
(35, 7,  CURDATE() + INTERVAL 2 DAY, '13:00', '14:00', FALSE),
(36, 7,  CURDATE() + INTERVAL 2 DAY, '14:00', '15:00', FALSE),
(37, 10, CURDATE() + INTERVAL 1 DAY, '10:00', '11:00', FALSE),
(38, 10, CURDATE() + INTERVAL 1 DAY, '11:00', '12:00', FALSE),
(39, 10, CURDATE() + INTERVAL 1 DAY, '17:00', '18:00', FALSE),
(40, 10, CURDATE() + INTERVAL 2 DAY, '10:00', '11:00', FALSE),
(41, 13, CURDATE() + INTERVAL 1 DAY, '09:00', '10:00', FALSE),
(42, 13, CURDATE() + INTERVAL 1 DAY, '10:00', '11:00', FALSE),
(43, 13, CURDATE() + INTERVAL 1 DAY, '11:00', '12:00', FALSE),
(44, 13, CURDATE() + INTERVAL 2 DAY, '09:00', '10:00', FALSE),
(45, 15, CURDATE() + INTERVAL 1 DAY, '10:00', '11:00', FALSE),
(46, 15, CURDATE() + INTERVAL 1 DAY, '11:00', '12:00', FALSE),
(47, 15, CURDATE() + INTERVAL 2 DAY, '10:00', '11:00', FALSE),
(48, 17, CURDATE() + INTERVAL 1 DAY, '13:00', '14:00', FALSE),
(49, 17, CURDATE() + INTERVAL 1 DAY, '14:00', '15:00', FALSE),
(50, 17, CURDATE() + INTERVAL 1 DAY, '20:00', '21:00', FALSE);

-- ============================================================
-- 8. RESERVAS
-- ============================================================
INSERT INTO reserva (id_reserva, estado_reserva, id_servicio, id_comercio, id_usuario, id_disponibilidad, fecha_creacion) VALUES
(1,  'CONFIRMADA', 1,  1,  2, 1,  NOW() - INTERVAL 5 DAY),
(2,  'FINALIZADO', 2,  2,  3, 9,  NOW() - INTERVAL 10 DAY),
(3,  'CONFIRMADA', 3,  4,  4, 14, NOW() - INTERVAL 3 DAY),
(4,  'PROCESO',    4,  5,  5, 21, NOW() - INTERVAL 1 DAY),
(5,  'CONFIRMADA', 5,  7,  6, 31, NOW() - INTERVAL 2 DAY),
(6,  'PENDIENTE',  6,  8,  7, 33, NOW()),
(7,  'FINALIZADO', 7,  1,  8, 3,  NOW() - INTERVAL 15 DAY),
(8,  'CONFIRMADA', 8,  6,  9, 26, NOW() - INTERVAL 4 DAY),
(9,  'PENDIENTE',  9,  4,  2, 19, NOW()),
(10, 'FINALIZADO', 10, 5,  3, 22, NOW() - INTERVAL 20 DAY),
(11, 'CONFIRMADA', 11, 10, 4, 37, NOW() - INTERVAL 2 DAY),
(12, 'CONFIRMADA', 12, 13, 5, 41, NOW() - INTERVAL 1 DAY),
(13, 'PENDIENTE',  13, 17, 6, 48, NOW()),
(14, 'PROCESO',    14, 15, 7, 45, NOW());

-- ============================================================
-- 9. PRODUCTOS
-- ============================================================
INSERT INTO producto (id_producto, nombre_producto, descripcion, imagen) VALUES
(1,  'Corte de pelo mujer',        'Corte personalizado con lavado y peinado',               '/images/productos/corte-mujer.jpg'),
(2,  'Corte de pelo hombre',       'Corte y arreglo de barba incluido',                      '/images/productos/corte-hombre.jpg'),
(3,  'Manicura completa',          'Manicura con esmaltado semipermanente',                   '/images/productos/manicura.jpg'),
(4,  'Pedicura spa',               'Pedicura con exfoliación y masaje',                       '/images/productos/pedicura.jpg'),
(5,  'Consulta medicina general',  'Revisión médica completa',                                '/images/productos/consulta-medica.jpg'),
(6,  'Sesión de fisioterapia',     'Sesión de 50 minutos con diagnóstico personalizado',      '/images/productos/fisioterapia.jpg'),
(7,  'Clase de yoga',              'Clase grupal de yoga de 60 minutos',                      '/images/productos/yoga.jpg'),
(8,  'Entrenamiento personal',     'Sesión individual con entrenador certificado',            '/images/productos/entrenamiento.jpg'),
(9,  'Curso de inglés B1',         'Curso intensivo de inglés nivel intermedio',              '/images/productos/curso-ingles.jpg'),
(10, 'Clase de matemáticas',       'Apoyo escolar personalizado de matemáticas',              '/images/productos/clase-mates.jpg'),
(11, 'Menú del día',               'Primer plato, segundo plato, postre y bebida',            '/images/productos/menu-dia.jpg'),
(12, 'Café con leche',             'Café de especialidad con leche de la casa',              '/images/productos/cafe.jpg'),
(13, 'Brunch completo',            'Brunch con café, zumo, tostada y bollería',               '/images/productos/brunch.jpg'),
(14, 'Reparación de móvil',        'Diagnóstico y reparación de pantalla y componentes',      '/images/productos/reparacion-movil.jpg'),
(15, 'Limpieza de ordenador',      'Limpieza interna y externa + cambio de pasta térmica',    '/images/productos/limpieza-pc.jpg'),
(16, 'Cesta ecológica',            'Fruta y verdura ecológica de temporada',                  '/images/productos/cesta-ecologica.jpg'),
(17, 'Masaje relajante',           'Masaje de cuerpo completo con aceites esenciales',        '/images/productos/masaje.jpg'),
(18, 'Tratamiento facial',         'Limpieza facial profunda con mascarilla personalizada',   '/images/productos/facial.jpg'),
(19, 'Blanqueamiento dental',      'Blanqueamiento dental con láser en una sola sesión',      '/images/productos/blanqueamiento.jpg'),
(20, 'Pizza margarita',            'Pizza margarita artesanal con horno de leña',             '/images/productos/pizza.jpg'),
(21, 'Curso de guitarra',          'Clases de guitarra para principiantes y nivel medio',     '/images/productos/guitarra.jpg'),
(22, 'Pase mensual gimnasio',      'Acceso ilimitado al gimnasio durante un mes',             '/images/productos/pase-gimnasio.jpg'),
(23, 'CrossFit mensual',           'Clases ilimitadas de CrossFit durante un mes',            '/images/productos/crossfit.jpg'),
(24, 'Reparación de PC',           'Diagnóstico y reparación de ordenadores y portátiles',    '/images/productos/reparacion-pc.jpg');

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
(7,  3,  16, 50,  15.00),
(8,  4,  5,  999, 50.00),
(9,  4,  6,  999, 35.00),
(10, 5,  7,  999, 12.00),
(11, 5,  8,  999, 30.00),
(12, 5,  22, 999, 49.90),
(13, 6,  9,  999, 120.00),
(14, 6,  10, 999, 25.00),
(15, 7,  11, 999, 14.50),
(16, 8,  12, 999, 2.50),
(17, 8,  13, 999, 12.00),
(18, 9,  14, 999, 65.00),
(19, 9,  15, 999, 35.00),
(20, 9,  24, 999, 45.00),
(21, 10, 17, 999, 50.00),
(22, 10, 18, 999, 40.00),
(23, 11, 16, 40,  12.00),
(24, 12, 16, 30,  18.00),
(25, 13, 19, 999, 80.00),
(26, 13, 5,  999, 45.00),
(27, 14, 6,  999, 40.00),
(28, 15, 23, 999, 55.00),
(29, 15, 8,  999, 35.00),
(30, 16, 21, 999, 50.00),
(31, 17, 20, 999, 12.00),
(32, 18, 24, 999, 45.00),
(33, 18, 14, 999, 60.00);

-- ============================================================
-- 11. RESEÑAS
-- ============================================================
INSERT INTO resena (id_resena, titulo, comentario, fecha, valoracion, autor_nombre, autor_email, id_comercio) VALUES
(1,  'Excelente servicio',        'Me encantó el trato recibido, volveré sin duda.',                NOW() - INTERVAL 15 DAY, 5, 'María García',     'maria.garcia@gmail.com',     1),
(2,  'Muy profesionales',         'Gran atención al cliente y resultados espectaculares.',          NOW() - INTERVAL 12 DAY, 4, 'Carlos López',    'carlos.lopez@gmail.com',     2),
(3,  'Productos de calidad',      'La fruta y verdura son fresquísimas y de temporada.',             NOW() - INTERVAL 10 DAY, 5, 'Ana Martínez',    'ana.martinez@gmail.com',     3),
(4,  'Atención excelente',        'La doctora fue muy atenta y resolvió todas mis dudas.',           NOW() - INTERVAL 8 DAY,  5, 'Javier Rodríguez', 'javier.rodriguez@gmail.com', 4),
(5,  'El mejor gimnasio',         'Instalaciones impecables y monitores muy profesionales.',         NOW() - INTERVAL 5 DAY,  5, 'Elena González',  'elena.gonzalez@gmail.com',   5),
(6,  'Clases muy útiles',         'El curso de inglés me ha ayudado muchísimo en mi trabajo.',       NOW() - INTERVAL 4 DAY,  4, 'Miguel Ramírez',  'miguel.ramirez@gmail.com',   6),
(7,  'Comida casera deliciosa',   'El menú del día está espectacular y a buen precio.',              NOW() - INTERVAL 2 DAY,  5, 'Carlos López',    'carlos.lopez@gmail.com',     7),
(8,  'Café de especialidad',      'El mejor café del barrio, y el brunch es increíble.',             NOW() - INTERVAL 1 DAY,  4, 'Ana Martínez',    'ana.martinez@gmail.com',     8),
(9,  'Arreglaron mi móvil',       'Rápidos, eficientes y me lo dejaron como nuevo.',                 NOW(),                    4, 'Javier Rodríguez', 'javier.rodriguez@gmail.com', 9),
(10, 'Corte perfecto',            'Salí encantada con mi nuevo corte de pelo.',                      NOW() - INTERVAL 9 DAY,  5, 'Laura Sánchez',   'laura.sanchez@gmail.com',    1),
(11, 'Paraíso de relax',          'El masaje relajante fue increíble, salí renovada.',                NOW() - INTERVAL 6 DAY,  5, 'María García',    'maria.garcia@gmail.com',     10),
(12, 'Muy profesional',           'Trato cercano y excelente atención al cliente.',                  NOW() - INTERVAL 4 DAY,  4, 'Laura Sánchez',   'laura.sanchez@gmail.com',    10),
(13, 'Fisioterapia de 10',        'Me recuperé de la lesión en tiempo récord. Muy recomendable.',    NOW() - INTERVAL 4 DAY,  5, 'Miguel Ramírez',  'miguel.ramirez@gmail.com',   14),
(14, 'Gran variedad',             'Tienen mucha variedad de productos ecológicos y de proximidad.',  NOW() - INTERVAL 6 DAY,  4, 'Elena González',  'elena.gonzalez@gmail.com',   11),
(15, 'Mejor carne del barrio',    'La carne de aquí es de primera calidad, repito cada semana.',     NOW() - INTERVAL 3 DAY,  5, 'David Fernández', 'david.fernandez@gmail.com',  12),
(16, 'Dentista genial',           'Por fin perdí el miedo al dentista. Trato exquisito.',            NOW() - INTERVAL 2 DAY,  5, 'Laura Sánchez',   'laura.sanchez@gmail.com',    13),
(17, 'CrossFit de verdad',        'Entrenamientos duros pero muy bien guiados. En forma en meses.',  NOW() - INTERVAL 1 DAY,  4, 'David Fernández', 'david.fernandez@gmail.com',  15),
(18, 'Clases de guitarra top',    'Aprendo muchísimo con cada clase. El profe es un crack.',         NOW(),                    5, 'Elena González',  'elena.gonzalez@gmail.com',   16),
(19, 'Pizza artesanal única',     'La mejor pizza del barrio, masa fina y horneada en horno de leña.',NOW() - INTERVAL 1 DAY,  5, 'Ana Martínez',    'ana.martinez@gmail.com',     17),
(20, 'Reparación rápida',         'Me arreglaron el portátil en 24 horas. Muy profesionales.',       NOW() - INTERVAL 2 DAY,  4, 'Miguel Ramírez',  'miguel.ramirez@gmail.com',   18);

-- ============================================================
-- 12. FAVORITOS
-- ============================================================
INSERT INTO cliente_favoritos_comercio (id_cliente, id_comercio) VALUES
(1, 1), (1, 3), (1, 7), (1, 10),
(2, 2), (2, 4), (2, 5), (2, 17),
(3, 1), (3, 8), (3, 11), (3, 13),
(4, 3), (4, 6), (4, 12), (4, 9),
(5, 5), (5, 7), (5, 14),
(6, 5), (6, 4), (6, 15), (6, 10),
(7, 1), (7, 2), (7, 8), (7, 16),
(8, 6), (8, 9), (8, 18);

-- ============================================================
-- 13. ESTADÍSTICAS
-- ============================================================
INSERT INTO estadisticas (id_estadistica, numero_visitas, puntuacion_media, total_ventas, id_comercio) VALUES
(1,  1240, 4.8, 156, 1),
(2,  980,  4.5, 112, 2),
(3,  2100, 4.7, 340, 3),
(4,  850,  4.9, 98,  4),
(5,  3100, 4.6, 420, 5),
(6,  560,  4.5, 65,  6),
(7,  1850, 4.9, 280, 7),
(8,  2200, 4.7, 310, 8),
(9,  780,  4.4, 89,  9),
(10, 450,  4.8, 45,  10),
(11, 620,  4.6, 78,  11),
(12, 390,  4.7, 52,  12),
(13, 280,  4.5, 34,  13),
(14, 350,  4.8, 42,  14),
(15, 180,  4.4, 28,  15),
(16, 120,  4.6, 15,  16),
(17, 410,  4.7, 55,  17),
(18, 230,  4.5, 31,  18);

-- ============================================================
-- 14. SOLICITUDES DE COLABORACIÓN
-- ============================================================
INSERT INTO solicitud_colaboracion (id_solicitud_colaboracion, nombre_comercio, nombre_titular, email_comercio, telefono_comercio, categoria, descripcion, estado, terminos_aceptados, fecha_creacion) VALUES
(1, 'Panadería El Trigal',      'Manuel Pérez',     'info@panaderiaeltrigal.com',  '612111222', 'Alimentación', 'Panadería artesana con horno de leña y masa madre.',                            'APROBADA', TRUE, NOW() - INTERVAL 60 DAY),
(2, 'Lavandería EcoClean',      'Sara Molina',      'info@ecoclean.com',           '622333444', 'Otros',        'Lavandería ecológica con recogida y entrega a domicilio.',                     'APROBADA', TRUE, NOW() - INTERVAL 45 DAY),
(3, 'Clínica Dental Care',      'Dr. Antonio Ruiz', 'citas@clinicadentalcare.com', '632555666', 'Salud',        'Clínica dental con ortodoncia, implantes y blanqueamiento.',                   'APROBADA', TRUE, NOW() - INTERVAL 30 DAY),
(4, 'Clases de Inglés Online',  'Marta Ruiz',       'info@inglesonline.com',      '642777888', 'Educación',    'Clases particulares de inglés online para todos los niveles.',                 'PENDIENTE', TRUE, NOW() - INTERVAL 10 DAY),
(5, 'Taller Mecánico Rápid',    'Javier Mora',      'info@tallermecano.com',      '652999111', 'Otros',        'Taller mecánico especializado en coches eléctricos e híbridos.',              'PENDIENTE', TRUE, NOW() - INTERVAL 5 DAY),
(6, 'Floristería El Jardín',    'Rosa Flores',      'info@floristeriajardin.com', '662888222', 'Otros',        'Floristería con flores frescas, ramos personalizados y plantas de interior.',  'RECHAZADA', TRUE, NOW() - INTERVAL 15 DAY);

-- ============================================================
-- 15. MENSAJES DE CONTACTO
-- ============================================================
INSERT INTO mensaje_contacto (id_mensaje_contacto, nombre, email, asunto, tipo, mensaje, fecha_creacion) VALUES
(1, 'Pedro Gómez',     'pedro.gomez@email.com',    'Quiero registrar mi comercio',   'registro',     'Tengo una frutería y me gustaría darme de alta en la plataforma.',              NOW() - INTERVAL 30 DAY),
(2, 'Rosa Martínez',   'rosa.martinez@email.com',  'Sugerencia de mejora',           'sugerencia',   'Sería genial si añadís un mapa para ver los comercios cercanos.',              NOW() - INTERVAL 20 DAY),
(3, 'Jorge Fernández', 'jorge.fernandez@email.com','Problema con una reserva',       'problema',     'Hice una reserva y no he recibido confirmación por email.',                    NOW() - INTERVAL 10 DAY),
(4, 'Ana Belén García','anabelen@email.com',       'Colaboración con el proyecto',   'colaboracion', 'Soy dueña de un gimnasio y me encantaría formar parte de DeTuBarrio.',         NOW() - INTERVAL 5 DAY),
(5, 'Carlos Ruiz',     'carlos.ruiz@email.com',    'Consulta sobre facturación',     'consulta',     'Necesito una factura de mi última compra en el Mercado Fresco.',               NOW() - INTERVAL 2 DAY),
(6, 'Lucía Vargas',    'lucia.vargas@email.com',   'Problema al registrarme',        'problema',     'Al intentar registrarme como comercio, el formulario no me deja seleccionar categoría.', NOW());

-- ============================================================
-- REACTIVAMOS FKs
-- ============================================================
SET FOREIGN_KEY_CHECKS = 1;
