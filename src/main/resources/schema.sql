-- Eliminar tablas si existen (sólo para desarrollo)
DROP TABLE IF EXISTS public.stock_log;
DROP TABLE IF EXISTS public.movimiento;
DROP TABLE IF EXISTS public.producto;

-- Crear tabla producto
CREATE TABLE public.producto (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(255),
    descripcion VARCHAR(255),
    categoria VARCHAR(255),
    precio NUMERIC(38,2),
    stock INTEGER NOT NULL,
    inventario_inicial INTEGER NOT NULL,
    factor_de_rotacion DOUBLE PRECISION NOT NULL,
    fecha_creacion TIMESTAMP
);

-- Crear tabla movimiento
CREATE TABLE public.movimiento (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(255) CHECK (tipo IN ('ENTRADA', 'SALIDA')),
    cantidad INTEGER NOT NULL,
    descripcion VARCHAR(255),
    fecha TIMESTAMP,
    producto_id BIGINT REFERENCES public.producto(id)
);

-- Crear tabla stock_log
CREATE TABLE public.stock_log (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT,
    cantidad_anterior INTEGER NOT NULL,
    cantidad_nueva INTEGER NOT NULL,
    operacion VARCHAR(255),
    fecha TIMESTAMP
);

-- Otorgar permisos al usuario remoto
ALTER TABLE public.producto OWNER TO remoto;
ALTER TABLE public.movimiento OWNER TO remoto;
ALTER TABLE public.stock_log OWNER TO remoto;