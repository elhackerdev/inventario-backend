-- Crear esquema si no existe
CREATE SCHEMA IF NOT EXISTS public;

-- Eliminar función si existe previamente
DROP FUNCTION IF EXISTS public.buscar_productos;

CREATE OR REPLACE FUNCTION public.buscar_productos(
    p_nombre TEXT DEFAULT NULL,
    p_categoria TEXT DEFAULT NULL,
    p_codigo TEXT DEFAULT NULL
)
RETURNS SETOF public.producto
LANGUAGE sql
AS $$
    SELECT * FROM public.producto
    WHERE (p_nombre IS NULL OR nombre ILIKE '%' || p_nombre || '%')
      AND (p_categoria IS NULL OR categoria = p_categoria)
      AND (p_codigo IS NULL OR codigo = p_codigo)
$$;