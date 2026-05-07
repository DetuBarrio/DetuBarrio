-- Flyway V1: Esquema completo + V2 ajustes + seed minimal
-- Uso: Flyway ejecutará esta migración al arrancar la aplicación sobre la BD configurada.
-- IMPORTANTE: No usar `CREATE DATABASE` ni `USE` aquí; Flyway aplica migraciones sobre la base de datos objetivo.

-- =====================
-- Esquema (Tablas principales)
-- =====================
CREATE TABLE persona (
    id_persona BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    direccion VARCHAR(150),
    ciudad VARCHAR(100),
    codigo_postal VARCHAR(10),
    foto_perfil VARCHAR(255)
);

CREATE TABLE usuario (
    id_usuario BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    rol ENUM('ADMIN', 'BASICO') NOT NULL DEFAULT 'BASICO',
    id_persona BIGINT NOT NULL,
    CONSTRAINT fk_usuario_persona FOREIGN KEY (id_persona) REFERENCES persona(id_persona)
);

CREATE TABLE trabajador (
    id_trabajador BIGINT PRIMARY KEY AUTO_INCREMENT,
    horario VARCHAR(100),
    num_horas_semanales INT,
    hora_entrada TIME,
    hora_salida TIME,
    cargo VARCHAR(80),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    id_persona BIGINT NOT NULL,
    CONSTRAINT fk_trabajador_persona FOREIGN KEY (id_persona) REFERENCES persona(id_persona)
);

CREATE TABLE contrato (
    id_contrato BIGINT PRIMARY KEY AUTO_INCREMENT,
    fecha_alta DATETIME NOT NULL,
    salario DECIMAL(10,2) NOT NULL,
    tipo_contrato VARCHAR(100),
    fecha_fin DATETIME,
    id_trabajador BIGINT NOT NULL,
    CONSTRAINT fk_contrato_trabajador FOREIGN KEY (id_trabajador) REFERENCES trabajador(id_trabajador)
);

CREATE TABLE categoria (
    id_categoria BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_categoria VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE comercio (
    id_comercio BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_comercio VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    horario VARCHAR(100),
    dias_apertura VARCHAR(100),
    logo VARCHAR(255),
    banner VARCHAR(255),
    id_categoria BIGINT NOT NULL,
    CONSTRAINT fk_comercio_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

CREATE TABLE estadisticas (
    id_estadistica BIGINT PRIMARY KEY AUTO_INCREMENT,
    numero_visitas INT NOT NULL DEFAULT 0,
    puntuacion_media DECIMAL(3,2),
    total_ventas INT NOT NULL DEFAULT 0,
    id_comercio BIGINT NOT NULL,
    CONSTRAINT fk_estadisticas_comercio FOREIGN KEY (id_comercio) REFERENCES comercio(id_comercio)
);

CREATE TABLE cliente (
    id_cliente BIGINT PRIMARY KEY AUTO_INCREMENT,
    ultimo_acceso DATETIME,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    nivel ENUM('NORMAL', 'PRO') NOT NULL DEFAULT 'NORMAL',
    id_persona BIGINT NOT NULL,
    CONSTRAINT fk_cliente_persona FOREIGN KEY (id_persona) REFERENCES persona(id_persona)
);

CREATE TABLE servicio (
    id_servicio BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_servicio ENUM('RESERVA', 'COMPRA') NOT NULL DEFAULT 'RESERVA',
    descripcion VARCHAR(255),
    fecha DATETIME,
    precio DECIMAL(10,2),
    metodo_pago ENUM('TARJETA', 'BIZUM'),
    id_cliente BIGINT NOT NULL,
    CONSTRAINT fk_servicio_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

CREATE TABLE reserva (
    id_reserva BIGINT PRIMARY KEY AUTO_INCREMENT,
    estado_reserva ENUM('PENDIENTE', 'PROCESO', 'FINALIZADO') NOT NULL,
    id_servicio BIGINT NOT NULL,
    CONSTRAINT fk_reserva_servicio FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio)
);

CREATE TABLE compra (
    id_compra BIGINT PRIMARY KEY AUTO_INCREMENT,
    estado_compra ENUM('PENDIENTE', 'REPARTO', 'FINALIZADO') NOT NULL,
    id_servicio BIGINT NOT NULL,
    CONSTRAINT fk_compra_servicio FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio)
);

CREATE TABLE producto (
    id_producto BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_producto VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE resena (
    id_resena BIGINT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(80) NOT NULL,
    comentario VARCHAR(255),
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valoracion TINYINT NOT NULL,
    autor_nombre VARCHAR(100) NOT NULL,
    autor_email VARCHAR(150),
    id_cliente BIGINT,
    id_comercio BIGINT NOT NULL,
    CONSTRAINT chk_valoracion CHECK (valoracion BETWEEN 1 AND 5),
    CONSTRAINT fk_resena_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_resena_comercio FOREIGN KEY (id_comercio) REFERENCES comercio(id_comercio)
);

CREATE TABLE trabajador_comercio (
    id_trabajador BIGINT NOT NULL,
    id_comercio BIGINT NOT NULL,
    PRIMARY KEY (id_trabajador, id_comercio),
    CONSTRAINT fk_trabajador_comercio_trabajador FOREIGN KEY (id_trabajador) REFERENCES trabajador(id_trabajador),
    CONSTRAINT fk_trabajador_comercio_comercio FOREIGN KEY (id_comercio) REFERENCES comercio(id_comercio)
);

CREATE TABLE cliente_favoritos_comercio (
    id_cliente BIGINT NOT NULL,
    id_comercio BIGINT NOT NULL,
    PRIMARY KEY (id_cliente, id_comercio),
    CONSTRAINT fk_favorito_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_favorito_comercio FOREIGN KEY (id_comercio) REFERENCES comercio(id_comercio)
);

CREATE TABLE compra_producto (
    id_compra_producto BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_compra BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    precio_unidad DECIMAL(10,2) NOT NULL,
    cantidad INT NOT NULL,
    hora DATETIME,
    CONSTRAINT fk_compra_producto_compra FOREIGN KEY (id_compra) REFERENCES compra(id_compra),
    CONSTRAINT fk_compra_producto_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

CREATE TABLE comercio_producto (
    id_comercio_producto BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_comercio BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    precio DECIMAL(10,2) NOT NULL,
    CONSTRAINT uk_comercio_producto UNIQUE (id_comercio, id_producto),
    CONSTRAINT fk_comercio_producto_comercio FOREIGN KEY (id_comercio) REFERENCES comercio(id_comercio),
    CONSTRAINT fk_comercio_producto_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

-- =====================
-- V2: ajustes sobre comercio (estado, fecha_solicitud, motivo_rechazo, id_usuario_creador)
-- =====================
ALTER TABLE comercio
  ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  ADD COLUMN fecha_solicitud DATETIME NULL,
  ADD COLUMN motivo_rechazo VARCHAR(500),
  ADD COLUMN id_usuario_creador BIGINT;

ALTER TABLE comercio
  ADD CONSTRAINT fk_comercio_usuario_creador FOREIGN KEY (id_usuario_creador) REFERENCES usuario(id_usuario);

CREATE INDEX idx_comercio_estado ON comercio(estado);

-- Rellenar fecha_solicitud si faltase
UPDATE comercio
SET fecha_solicitud = COALESCE(fecha_solicitud, CURRENT_TIMESTAMP)
WHERE fecha_solicitud IS NULL;

-- Actualizar comercios existentes a APROBADO si estaban sin estado y sin creador
UPDATE comercio SET estado = 'APROBADO' WHERE estado = 'PENDIENTE' AND id_usuario_creador IS NULL;

-- =====================
-- Seed minimal reproducible
-- =====================
-- Personas
INSERT INTO persona (id_persona, nombre, apellidos, email, telefono, direccion, ciudad, codigo_postal, foto_perfil) VALUES
(1, 'Ana', 'García', 'ana@detubarrio.local', '600111222', 'C/ Mayor 1', 'Oviedo', '33001', '/images/ana.jpg'),
(2, 'Pablo', 'López', 'pablo@detubarrio.local', '600333444', 'C/ Luna 2', 'Gijón', '33002', '/images/pablo.jpg');

-- Usuarios (hashes de ejemplo; asegurar algoritmo BCrypt en la app)
INSERT INTO usuario (id_usuario, username, password_hash, fecha_registro, rol, id_persona) VALUES
(1, 'ana', '$2a$10$7Qx1eE2y3Zq9h1Gf7kV/eOqKf1z9Yc8b0Lq1YvK6bS9uJ2c3d4eFG', '2026-05-06 10:00:00', 'ADMIN', 1),
(2, 'pablo', '$2a$10$7Qx1eE2y3Zq9h1Gf7kV/eOqKf1z9Yc8b0Lq1YvK6bS9uJ2c3d4eFG', '2026-05-06 10:00:00', 'BASICO', 2);

-- Categorías
INSERT INTO categoria (id_categoria, nombre_categoria, descripcion) VALUES
(1, 'Hostelería', 'Restaurantes, cafeterías y bares'),
(2, 'Comercio', 'Tiendas y comercios minoristas');

-- Comercios
INSERT INTO comercio (id_comercio, nombre_comercio, descripcion, horario, dias_apertura, logo, banner, id_categoria, estado, fecha_solicitud) VALUES
(1, 'Café Central', 'Cafetería con productos locales y terraza', '08:00-22:00', 'Lunes-Domingo', '/images/logo_central.png', '/images/banner_central.jpg', 1, 'APROBADO', CURRENT_TIMESTAMP);

-- Estadísticas básicas
INSERT INTO estadisticas (id_estadistica, numero_visitas, puntuacion_media, total_ventas, id_comercio) VALUES
(1, 1250, 4.5, 320, 1);

-- Cliente
INSERT INTO cliente (id_cliente, ultimo_acceso, estado, nivel, id_persona) VALUES
(1, NOW(), TRUE, 'PRO', 2);

-- Productos
INSERT INTO producto (id_producto, nombre_producto, descripcion) VALUES
(1, 'Café Espresso', 'Café espresso de tueste medio, servido en taza pequeña');

-- Comercio-Producto
INSERT INTO comercio_producto (id_comercio_producto, id_comercio, id_producto, stock, precio) VALUES
(1, 1, 1, 120, 1.50);

-- Reseña
INSERT INTO resena (id_resena, titulo, comentario, fecha, valoracion, autor_nombre, autor_email, id_cliente, id_comercio) VALUES
(1, 'Excelente café', 'Ambiente agradable y personal atento.', NOW(), 5, 'Pablo López', 'pablo@detubarrio.local', 1, 1);

-- Fin V1 all-in-one
