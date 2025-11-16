-- ==========================================================
-- CREACIÓN DE TABLAS
-- ==========================================================

-- ==========================
-- TABLA ROL (catálogo para combos)
-- ==========================
CREATE TABLE Rol (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(50)
);

-- ==========================
-- TABLA LOTE (catálogo para combos)
-- ==========================
CREATE TABLE Lote (
    id_lote SERIAL PRIMARY KEY,
    nombre VARCHAR(20)
);

-- ==========================
-- TABLA NÚMERO DE CASA (catálogo para combos)
-- ==========================
CREATE TABLE NumeroCasa (
    id_numero_casa SERIAL PRIMARY KEY,
    numero VARCHAR(10)
);

select * from usuarios;

DELETE FROM usuarios
WHERE id_usuario = 11;


-- ==========================
-- TABLA USUARIOS
-- ==========================
CREATE TABLE Usuarios (
    id_usuario SERIAL PRIMARY KEY,
    dpi VARCHAR(13),
    nombre VARCHAR(40),
    apellido VARCHAR(40),
    usuario VARCHAR(20),
    correo VARCHAR(100),
    contrasena VARCHAR(100),
    rol VARCHAR(20),
    lote VARCHAR(20),
    numero_casa VARCHAR(10),
    estado VARCHAR(20) DEFAULT 'activo',
    fecha_creacion DATE DEFAULT CURRENT_DATE,
    CONSTRAINT chk_rol CHECK (rol IN ('residente', 'administrador', 'seguridad')),
    CONSTRAINT chk_estado_usuario CHECK (estado IN ('activo', 'inactivo'))
);



CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_usuarios_nombre_trgm ON usuarios USING gin (nombre gin_trgm_ops);
CREATE INDEX idx_usuarios_apellido_trgm ON usuarios USING gin (apellido gin_trgm_ops);

CREATE INDEX idx_usuarios_lote ON usuarios (lote);
CREATE INDEX idx_usuarios_numero_casa ON usuarios (numero_casa);




SELECT version();

SELECT * 
FROM usuarios 
;



update usuarios set estado = 'activo' where id_usuario =7;

--- tarjeta
CREATE TABLE Tarjeta (
    id_tarjeta SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES Usuarios(id_usuario) ON DELETE CASCADE,
    nombre_tarjeta VARCHAR(50), -- Alias de la tarjeta (ej: "Visa Principal")
    numero_tarjeta VARCHAR(20),
    fecha_vencimiento DATE,
    cvv VARCHAR(4),
    nombre_titular VARCHAR(100),
    tipo_tarjeta VARCHAR(20), -- Crédito o Débito
    saldo DECIMAL(10,2)
);

-- ==========================
-- TABLA TIPO DE PAGO (catálogo)
-- ==========================
CREATE TABLE TipoPago (
    id_tipo SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE,
    monto DECIMAL(10,2)
);

-- ==========================
-- TABLA METODO DE PAGO (catálogo)
-- ==========================
CREATE TABLE MetodoPago (
    id_metodo SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE
);

-- Inserts iniciales para catálogo
INSERT INTO MetodoPago (nombre) VALUES
('Efectivo'),
('Transferencia'),
('Tarjeta'),
('Otro');


select * from tipopago t ;
-- ==========================
-- TABLA PAGO
-- ==========================

CREATE TABLE Pago (
    id_pago SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES Usuarios(id_usuario) ON DELETE CASCADE,
    id_tipo INTEGER REFERENCES TipoPago(id_tipo) ON DELETE CASCADE,
    id_metodo INTEGER REFERENCES MetodoPago(id_metodo) ON DELETE CASCADE,
    id_tarjeta INTEGER REFERENCES Tarjeta(id_tarjeta) ON DELETE CASCADE, -- opcional
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    monto DECIMAL(10,2) NOT NULL,
    mora DECIMAL(10,2) DEFAULT 0,
    observaciones TEXT,
    estado VARCHAR(20) DEFAULT 'pendiente'
        CHECK (estado IN ('pendiente','confirmado','rechazado','en_proceso')),
    
    -- 🔹 Nuevos campos para controlar qué período se paga
    mes_pagado INTEGER CHECK (mes_pagado BETWEEN 1 AND 12),
    anio_pagado INTEGER CHECK (anio_pagado >= 2000)
);

TRUNCATE TABLE pago RESTART IDENTITY CASCADE;

select * from usuarios u ;

INSERT INTO Pago (
    id_usuario,
    id_tipo,
    id_metodo,
    id_tarjeta,
    monto,
    mora,
    observaciones,
    estado,
    mes_pagado,
    anio_pagado
) VALUES (
    7,
    2,
    1,
    NULL,
    250.00,
    0.00,
    'Multa por incumplimiento de reglamento',
    'pendiente',
    NULL,
    NULL
);



select * from pago;
INSERT INTO Pago (
    id_usuario, 
    id_tipo, 
    id_metodo, 
    id_tarjeta, 
    monto, 
    mora, 
    observaciones, 
    estado
) VALUES (
    7,              -- Usuario residente
    1,              -- Tipo de pago (ej: mantenimiento)
    1,              -- Método (ej: efectivo)
    NULL,           -- No aplica tarjeta
    500.00,         -- Monto pagado
    0.00,           -- Mora
    'Pago de mantenimiento mensual enero 2025',
    'confirmado'    -- Estado
);
INSERT INTO Pago (
    id_usuario, 
    id_tipo, 
    id_metodo, 
    id_tarjeta, 
    monto, 
    mora, 
    observaciones, 
    estado
) VALUES (
    7, 
    1, 
    2,              -- Supongamos que 2 = Tarjeta
    1,              -- id_tarjeta = 1 (Visa Principal)
    600.00, 
    25.00, 
    'Pago de mantenimiento con mora - febrero 2025',
    'confirmado'
);


-- ==========================
-- TABLA QR_USUARIO
-- ==========================
CREATE TABLE QR_Usuario (
    id_qr_usuario SERIAL PRIMARY KEY,
    codigo_qr_usuario VARCHAR(100),
    id_usuario INTEGER REFERENCES Usuarios(id_usuario) ON DELETE CASCADE,
    fecha_generada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(30),
    estado VARCHAR(20) DEFAULT 'activo',
    ruta_qr VARCHAR(255),
    CONSTRAINT chk_estado_qrusuario CHECK (estado IN ('activo', 'inactivo'))
);

select * from qr_usuario qu ;
select * from qr_visita qv ;


-- ==========================
-- TABLA ACCESO_USUARIO
-- ==========================
CREATE TABLE Acceso_Usuario (
    id_acceso SERIAL PRIMARY KEY,
    id_qr_usuario INTEGER REFERENCES QR_Usuario(id_qr_usuario) ON DELETE CASCADE,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(30)
);

select * from acceso_usuario au ;

-- ==========================
-- TABLA NOTIFICACIONES
-- ==========================
CREATE TABLE Notificaciones (
    id_notificacion SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES Usuarios(id_usuario) ON DELETE CASCADE,
    asunto VARCHAR(100),
    mensaje TEXT,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_evento VARCHAR(50)
);

-- ==========================
-- TABLA VISITA
-- ==========================
CREATE TABLE Visita (
    id_visita SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES Usuarios(id_usuario) ON DELETE CASCADE,
    dpi_visita VARCHAR(13),
    nombre VARCHAR(100),
    tipo_visita VARCHAR(30) CHECK (tipo_visita IN ('Visita', 'Por intentos')),
    correo_visita VARCHAR(100),
    estado VARCHAR(20) DEFAULT 'activo' CHECK (estado IN ('activo', 'cancelado'))
);

ALTER TABLE Visita
ADD COLUMN motivo VARCHAR(255);

TRUNCATE TABLE visita RESTART IDENTITY CASCADE;

TRUNCATE TABLE Acceso_Visita RESTART IDENTITY CASCADE;


select * from visita v ;



select * from Acceso_Visita;

select * from qr_visita qv; 



-- ==========================
-- TABLA QR_VISITA
-- ==========================
CREATE TABLE QR_Visita (
    id_qr_visita SERIAL PRIMARY KEY,
    id_visita INTEGER REFERENCES Visita(id_visita) ON DELETE CASCADE,
    codigo_qr_visita VARCHAR(100),
    valido_hasta TIMESTAMP,   -- aplica para visitas con fecha
    intentos INTEGER          -- aplica para "por intentos"
);

ALTER TABLE QR_Visita
ADD COLUMN ruta_qr VARCHAR(255);

TRUNCATE TABLE QR_Visita RESTART IDENTITY CASCADE;


-- ==========================
-- TABLA ACCESO_VISITA
-- ==========================
CREATE TABLE Acceso_Visita (
    id_acceso_visita SERIAL PRIMARY KEY,
    id_qr_visita INTEGER REFERENCES QR_Visita(id_qr_visita) ON DELETE CASCADE,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(30) -- 'entrada' o 'salida'
);


select * from acceso_usuario au;

-- ==========================
-- TABLA NOTIFICACIONES_VISITA
-- ==========================
CREATE TABLE Notificaciones_Visita (
    id_notificacion_visita SERIAL PRIMARY KEY,
    id_visita INTEGER REFERENCES Visita(id_visita) ON DELETE CASCADE,
    asunto VARCHAR(100),
    mensaje TEXT,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_evento VARCHAR(50)
);

-- ==========================
-- TABLA BITÁCORA
-- ==========================
CREATE TABLE Bitacora (
    id_bitacora SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES Usuarios(id_usuario) ON DELETE CASCADE,
    accion VARCHAR(100),
    modulo VARCHAR(100),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================
-- TABLA ÁREA COMÚN
-- ==========================
CREATE TABLE AreaComun (
    id_area SERIAL PRIMARY KEY,
    nombre VARCHAR(50),
    descripcion TEXT,
    capacidad INT,
    estado VARCHAR(20) DEFAULT 'activo',
    CONSTRAINT chk_estado_area CHECK (estado IN ('activo', 'inactivo'))
);

-- ==========================
-- TABLA RESERVA
-- ==========================
CREATE TABLE Reserva (
    id_reserva SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES Usuarios(id_usuario) ON DELETE CASCADE,
    id_area INTEGER REFERENCES AreaComun(id_area) ON DELETE CASCADE,
    fecha_reserva DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(20) DEFAULT 'Activa',
    CONSTRAINT chk_estado_reserva CHECK (estado IN ('Activa', 'Cancelada', 'Finalizada'))
);

select * from reserva r ;
select * from pago p ;

-- ==========================================================
-- INSERCIONES DE DATOS
-- ==========================================================

INSERT INTO Tarjeta (
    id_usuario, 
    nombre_tarjeta, 
    numero_tarjeta, 
    fecha_vencimiento, 
    cvv, 
    nombre_titular, 
    tipo_tarjeta, 
    saldo
) VALUES (
    7, 
    'Visa Principal', 
    '4111111111111111', 
    '2027-12-31', 
    '123', 
    'manuel', 
    'Crédito', 
    5000.00
);

-- Mastercard Débito
INSERT INTO Tarjeta (
    id_usuario, 
    nombre_tarjeta, 
    numero_tarjeta, 
    fecha_vencimiento, 
    cvv, 
    nombre_titular, 
    tipo_tarjeta, 
    saldo
) VALUES (
    7, 
    'Mastercard Débito', 
    '5500000000000004', 
    '2026-08-31', 
    '456', 
    'manuel', 
    'Débito', 
    3200.00
);

-- Visa Secundaria
INSERT INTO Tarjeta (
    id_usuario, 
    nombre_tarjeta, 
    numero_tarjeta, 
    fecha_vencimiento, 
    cvv, 
    nombre_titular, 
    tipo_tarjeta, 
    saldo
) VALUES (
    7, 
    'Visa Secundaria', 
    '4000000000000002', 
    '2028-05-31', 
    '789', 
    'manuel', 
    'Crédito', 
    1500.00
);

-- American Express
INSERT INTO Tarjeta (
    id_usuario, 
    nombre_tarjeta, 
    numero_tarjeta, 
    fecha_vencimiento, 
    cvv, 
    nombre_titular, 
    tipo_tarjeta, 
    saldo
) VALUES (
    7, 
    'Amex Gold', 
    '371449635398431', 
    '2029-03-31', 
    '321', 
    'manuel', 
    'Crédito', 
    7000.00
);



-- ROLES
INSERT INTO Rol (nombre) VALUES
('residente'),
('administrador'),
('seguridad');

INSERT INTO Rol (nombre) VALUES
('otros');

DELETE FROM Rol 
WHERE nombre = 'otros';


-- LOTES
INSERT INTO Lote (nombre) VALUES
('Lote 1'),
('Lote 5');

-- LOTES (1 al 50)
INSERT INTO Lote (nombre) VALUES
('Lote 1'), ('Lote 2'), ('Lote 3'), ('Lote 4'), ('Lote 5'),
('Lote 6'), ('Lote 7'), ('Lote 8'), ('Lote 9'), ('Lote 10'),
('Lote 11'), ('Lote 12'), ('Lote 13'), ('Lote 14'), ('Lote 15'),
('Lote 16'), ('Lote 17'), ('Lote 18'), ('Lote 19'), ('Lote 20'),
('Lote 21'), ('Lote 22'), ('Lote 23'), ('Lote 24'), ('Lote 25'),
('Lote 26'), ('Lote 27'), ('Lote 28'), ('Lote 29'), ('Lote 30'),
('Lote 31'), ('Lote 32'), ('Lote 33'), ('Lote 34'), ('Lote 35'),
('Lote 36'), ('Lote 37'), ('Lote 38'), ('Lote 39'), ('Lote 40'),
('Lote 41'), ('Lote 42'), ('Lote 43'), ('Lote 44'), ('Lote 45'),
('Lote 46'), ('Lote 47'), ('Lote 48'), ('Lote 49'), ('Lote 50');


-- NÚMEROS DE CASA
INSERT INTO NumeroCasa (numero) VALUES
('12-A'),
('15-B');

-- NÚMEROS DE CASA (1-A a 1-AX → total 50)
INSERT INTO NumeroCasa (numero) VALUES
('1-A'), ('1-B'), ('1-C'), ('1-D'), ('1-E'),
('1-F'), ('1-G'), ('1-H'), ('1-I'), ('1-J'),
('1-K'), ('1-L'), ('1-M'), ('1-N'), ('1-O'),
('1-P'), ('1-Q'), ('1-R'), ('1-S'), ('1-T'),
('1-U'), ('1-V'), ('1-W'), ('1-X'), ('1-Y'),
('1-Z'), ('1-AA'), ('1-AB'), ('1-AC'), ('1-AD'),
('1-AE'), ('1-AF'), ('1-AG'), ('1-AH'), ('1-AI'),
('1-AJ'), ('1-AK'), ('1-AL'), ('1-AM'), ('1-AN'),
('1-AO'), ('1-AP'), ('1-AQ'), ('1-AR'), ('1-AS'),
('1-AT'), ('1-AU'), ('1-AV'), ('1-AW'), ('1-AX');


-- USUARIOS
INSERT INTO Usuarios (dpi, nombre, apellido, usuario, correo, contrasena, rol, lote, numero_casa, estado)
VALUES ('1234567890123', 'José', 'Manuel', 'josema', 'sipacchuquiejj@gmail.com', '123', 'administrador', 'Lote 1', '12-A', 'activo');

INSERT INTO Usuarios (dpi, nombre, apellido, usuario, correo, contrasena, rol, lote, numero_casa, estado)
VALUES ('1234567894123', 'José', 'Seguridad', 'josema12', 'sipacchuquiejj@gmail.com', '125', 'seguridad', 'Lote 5', '12-A', 'activo');

-- TARJETAS
INSERT INTO Tarjeta (id_usuario, numero_tarjeta, fecha_vencimiento, cvv, nombre_titular, tipo_tarjeta, saldo)
VALUES (1, '4111111111111111', '2027-08-31', '123', 'José Manuel', 'Visa', 1500.00);

INSERT INTO Tarjeta (id_usuario, numero_tarjeta, fecha_vencimiento, cvv, nombre_titular, tipo_tarjeta, saldo)
VALUES (2, '4111111111111111', '2026-12-31', '123', 'Hades', 'Visa', 2000.00);

-- TIPOS DE PAGO
INSERT INTO TipoPago (nombre, monto) VALUES
('Mantenimiento', 550.00),
('Multa', 250.00),
('Reinstalación de servicios', 750.00);

INSERT INTO TipoPago (nombre, monto) VALUES
('Jardinería', 300.00),
('Emergencias', 1000.00);

UPDATE TipoPago
SET nombre = 'Reinstalacion de servicios'
WHERE nombre = 'Reinstalación de servicios';

INSERT INTO Pago (
    id_usuario, 
    id_tipo, 
    id_metodo, 
    id_tarjeta, 
    monto, 
    mora, 
    observaciones, 
    estado
) VALUES (
    7,              -- id_usuario
    2,              -- id_tipo = 2 → Multa
    2,              -- Supongamos que 2 = Tarjeta
    1,              -- id_tarjeta = 1 (Visa Principal)
    250.00,         -- Monto de la multa (según catálogo TipoPago)
    0.00,           -- Multa no genera mora adicional
    'Multa por atraso en el pago de mantenimiento - febrero 2025',
    'confirmado'
);

INSERT INTO Pago (
    id_usuario, 
    id_tipo, 
    id_metodo, 
    id_tarjeta, 
    monto, 
    mora, 
    observaciones, 
    estado
) VALUES (
    7,                -- id_usuario
    2,                -- id_tipo = 2 → Multa
    2,                -- id_metodo = 2 (ej: tarjeta)
    1,                -- id_tarjeta = 1 (Visa Principal)
    250.00,           -- Monto de la multa (según catálogo TipoPago)
    0.00,             -- Multa no genera mora
    'Multa por estacionarse en un área prohibida - febrero 2025',
    'confirmado'
);




-- ÁREAS COMUNES
INSERT INTO AreaComun (nombre, descripcion, capacidad) VALUES
('Salón de eventos', 'Espacio para reuniones y celebraciones', 80),
('Piscina', 'Área recreativa con piscina comunitaria', 40),
('Cancha de fútbol', 'Campo de césped sintético para uso deportivo', 22),
('Gimnasio', 'Espacio con equipo de ejercicios', 20),
('Cancha de baloncesto', 'Cancha techada de uso compartido', 10),
('Área de juegos infantiles', 'Zona de juegos para niños pequeños', 30),
('Área de BBQ', 'Espacio con parrillas para asados familiares', 15),
('Terraza', 'Terraza con mesas y sillas para convivencias', 25),
('Sala de cine', 'Mini cine comunitario con proyector y butacas', 15),
('Sala de juntas', 'Salón de reuniones ejecutivas o administrativas', 12),
('Parqueo de visitas', 'Espacios designados para vehículos de visitantes', 50),
('Área de coworking', 'Espacio con escritorios y conexión a internet', 20),
('Pista de jogging', 'Circuito para correr dentro del condominio', 30),
('Cancha de tenis', 'Cancha de tenis con iluminación nocturna', 4),
('Roof Garden', 'Área verde en la azotea para convivencia', 25);

TRUNCATE AreaComun RESTART IDENTITY CASCADE;

-- ==========================================================
-- CONSULTAS SELECT
-- ==========================================================

SELECT * FROM Usuarios;
SELECT * FROM Tarjeta;
SELECT * FROM TipoPago; 
SELECT * FROM Pago;
SELECT * FROM QR_Usuario;
SELECT * FROM Acceso_Usuario;
SELECT * FROM Bitacora;
SELECT * FROM AreaComun;
SELECT * FROM Reserva;

-- BITÁCORA CON JOINS
SELECT b.id_bitacora, u.nombre, u.apellido, u.usuario, b.accion, b.modulo, b.fecha_hora
FROM Bitacora b
JOIN Usuarios u ON b.id_usuario = u.id_usuario
ORDER BY b.fecha_hora DESC;

SELECT b.id_bitacora, ua.nombre AS nombre_actor, ua.apellido AS apellido_actor,
       uf.nombre AS nombre_afectado, uf.apellido AS apellido_afectado,
       b.accion, b.fecha_hora
FROM Bitacora b
JOIN Usuarios ua ON b.id_usuario = ua.id_usuario
LEFT JOIN Usuarios uf 
     ON uf.id_usuario = CASE
         WHEN regexp_replace(b.accion, '[^0-9]', '', 'g') ~ '^[0-9]+$'
         THEN CAST(regexp_replace(b.accion, '[^0-9]', '', 'g') AS INT)
         ELSE NULL
     END
ORDER BY b.fecha_hora DESC;

-- ==========================================================
-- OPERACIONES ESPECIALES
-- ==========================================================

TRUNCATE TABLE reserva RESTART IDENTITY CASCADE;
TRUNCATE TABLE Acceso_Usuario RESTART IDENTITY CASCADE;

TRUNCATE TABLE pago RESTART IDENTITY CASCADE;

TRUNCATE TABLE mensaje RESTART IDENTITY CASCADE;



CREATE TABLE Paqueteria (
    id_paqueteria SERIAL PRIMARY KEY,
    numero_guia VARCHAR(30),
    id_residente INT,
    id_agente_registra INT,
    id_agente_entrega INT,
    casa_residente VARCHAR(20),
    estado VARCHAR(20) DEFAULT 'pendiente',
    observaciones VARCHAR(200),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega TIMESTAMP,
    FOREIGN KEY (id_residente) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_agente_registra) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_agente_entrega) REFERENCES Usuarios(id_usuario)
);

 
select * from paqueteria;

INSERT INTO Paqueteria (
    numero_guia,
    id_residente,
    id_agente_registra,
    id_agente_entrega,
    casa_residente,
    estado,
    observaciones
) VALUES (
    'GT-20251010-00',   -- número de guía
    7,                   -- id del residente (Manuel)
    12,                  -- id del agente que registra (José)
    NULL,                -- aún no entregado
    '1-AK',              -- número de casa del residente
    'pendiente',         -- estado inicial
    'Paquete recibido en garita principal'  -- observaciones
);

SELECT 
    p.id_paqueteria,
    p.numero_guia,
    CONCAT(r.nombre, ' ', r.apellido) AS residente,
    r.lote AS lote_residente,
    p.casa_residente,
    CONCAT(a.nombre, ' ', a.apellido) AS agente_registra,
    CONCAT(e.nombre, ' ', e.apellido) AS agente_entrega,
    p.estado,
    p.observaciones,
    p.fecha_registro,
    p.fecha_entrega
FROM 
    Paqueteria p
LEFT JOIN Usuarios r ON p.id_residente = r.id_usuario
LEFT JOIN Usuarios a ON p.id_agente_registra = a.id_usuario
LEFT JOIN Usuarios e ON p.id_agente_entrega = e.id_usuario
ORDER BY p.id_paqueteria DESC;

-- activa la conexio y crea los indices
-- Extensión de trigramas (si no existe)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Índices GIN para búsquedas rápidas con ILIKE
CREATE INDEX IF NOT EXISTS idx_paqueteria_numero_guia_trgm 
  ON paqueteria USING gin (numero_guia gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_paqueteria_casa_residente_trgm 
  ON paqueteria USING gin (casa_residente gin_trgm_ops);

-- En este caso “residente” no existe directamente en la tabla,
-- por lo tanto indexamos las columnas nombre y apellido de Usuarios.
CREATE INDEX IF NOT EXISTS idx_usuarios_nombre_trgm 
  ON usuarios USING gin (nombre gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_usuarios_apellido_trgm 
  ON usuarios USING gin (apellido gin_trgm_ops);


DROP TABLE IF EXISTS conversacion CASCADE;

CREATE TABLE conversacion (
    id_conversacion SERIAL PRIMARY KEY,
    id_residente INT REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_guardia INT REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_administrador INT REFERENCES usuarios(id_usuario) ON DELETE SET NULL,
    tipo_conversacion VARCHAR(30),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'activa',
    ultimo_mensaje TIMESTAMP
);


CREATE TABLE mensaje (
    id_mensaje SERIAL PRIMARY KEY,
    id_conversacion INT REFERENCES conversacion(id_conversacion) ON DELETE CASCADE,
    id_emisor INT REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_mensaje_respuesta INT NULL REFERENCES mensaje(id_mensaje) ON DELETE SET NULL, -- 🔹 nueva columna
    contenido TEXT,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leido BOOLEAN DEFAULT FALSE,
    tipo VARCHAR(20) DEFAULT 'texto'
);

select * from mensaje;

CREATE TABLE catalogo_incidente (
    id_tipo_incidente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL
);

INSERT INTO catalogo_incidente (nombre) VALUES
('Disturbios'),
('Ruido'),
('Accidente vehicular'),
('Daños inmobiliarios'),
('Otros');


select * from catalogo_incidente ci ;

CREATE TABLE incidente (
    id_incidente SERIAL PRIMARY KEY,
    id_residente INT REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_guardia INT REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_tipo_incidente INT REFERENCES catalogo_incidente(id_tipo_incidente) ON DELETE CASCADE,
    descripcion TEXT,
    fecha_reporte TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'pendiente',
    prioridad VARCHAR(20) DEFAULT 'media'
);


select * from incidente i ;


CREATE TABLE archivo_mensaje (
    id_archivo SERIAL PRIMARY KEY,
    id_mensaje INT REFERENCES mensaje(id_mensaje) ON DELETE CASCADE,
    ruta_archivo VARCHAR(255),
    tipo_mime VARCHAR(100),
    nombre_original VARCHAR(255),
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

SELECT * FROM conversacion ORDER BY id_conversacion DESC;

 select * from conversacion;
 
 
 select * from mensaje;
 
 SELECT * FROM usuarios;

 
TRUNCATE TABLE conversacion RESTART IDENTITY CASCADE;

TRUNCATE TABLE mensaje RESTART IDENTITY CASCADE;

 CREATE TABLE TipoInconveniente (
    id_tipo_inconveniente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL
);

INSERT INTO TipoInconveniente (nombre) VALUES
('Lentitud en el sistema'),
('Error al realizar una acción'),
('Error al acceder a una opción'),
('Error de visualización'),
('Otros');

CREATE TABLE ReporteMantenimiento (
    id_reporte SERIAL PRIMARY KEY,
    id_residente INT REFERENCES Usuarios(id_usuario) ON DELETE CASCADE,
    id_tipo_inconveniente INT REFERENCES TipoInconveniente(id_tipo_inconveniente) ON DELETE SET NULL,
    descripcion TEXT,
    fecha_incidente TIMESTAMP
);

select * from incidente; 

select * from ReporteMantenimiento; 

CREATE DATABASE empresa;



select * from usuarios;
select * from bitacora;
