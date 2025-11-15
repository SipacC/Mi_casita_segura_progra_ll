--tablas principales


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

CREATE TABLE Tarjeta (
    id_tarjeta SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES Usuarios(id_usuario) ON DELETE CASCADE,
    nombre_tarjeta VARCHAR(50),
    numero_tarjeta VARCHAR(20),
    fecha_vencimiento DATE,
    cvv VARCHAR(4),
    nombre_titular VARCHAR(100),
    tipo_tarjeta VARCHAR(20),
    saldo DECIMAL(10,2)
);


CREATE TABLE Pago (
    id_pago SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES Usuarios(id_usuario) ON DELETE CASCADE,
    id_tipo INTEGER REFERENCES TipoPago(id_tipo) ON DELETE CASCADE,
    id_metodo INTEGER REFERENCES MetodoPago(id_metodo) ON DELETE CASCADE,
    id_tarjeta INTEGER REFERENCES Tarjeta(id_tarjeta) ON DELETE CASCADE,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    monto DECIMAL(10,2) NOT NULL,
    mora DECIMAL(10,2) DEFAULT 0,
    observaciones TEXT,
    estado VARCHAR(20) DEFAULT 'pendiente'
        CHECK (estado IN ('pendiente','confirmado','rechazado','en_proceso')),
    mes_pagado INTEGER CHECK (mes_pagado BETWEEN 1 AND 12),
    anio_pagado INTEGER CHECK (anio_pagado >= 2000)
);







CREATE TABLE Bitacora (
    id_bitacora SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES Usuarios(id_usuario) ON DELETE CASCADE,
    accion VARCHAR(100),
    modulo VARCHAR(100),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

CREATE TABLE ReporteMantenimiento (
    id_reporte SERIAL PRIMARY KEY,
    id_residente INT REFERENCES Usuarios(id_usuario) ON DELETE CASCADE,
    id_tipo_inconveniente INT REFERENCES TipoInconveniente(id_tipo_inconveniente) ON DELETE SET NULL,
    descripcion TEXT,
    fecha_incidente TIMESTAMP
);




--catalogos

CREATE TABLE TipoInconveniente (
    id_tipo_inconveniente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE catalogo_incidente (
    id_tipo_incidente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE AreaComun (
    id_area SERIAL PRIMARY KEY,
    nombre VARCHAR(50),
    descripcion TEXT,
    capacidad INT,
    estado VARCHAR(20) DEFAULT 'activo',
    CONSTRAINT chk_estado_area CHECK (estado IN ('activo', 'inactivo'))
);


