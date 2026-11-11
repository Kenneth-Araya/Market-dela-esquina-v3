CREATE SCHEMA IF NOT EXISTS bd_administracion;

CREATE TABLE IF NOT EXISTS bd_administracion.configuracion_tienda (
    id BIGINT PRIMARY KEY,
    nombre_tienda VARCHAR(100) NOT NULL,
    correo_contacto VARCHAR(100),
    telefono_contacto VARCHAR(20),
    moneda VARCHAR(3) NOT NULL,
    direccion_tienda VARCHAR(300),
    horario_atencion VARCHAR(100),
    notificar_nuevas_ventas BOOLEAN NOT NULL,
    modo_mantenimiento BOOLEAN NOT NULL,
    CONSTRAINT ck_configuracion_tienda_registro_unico
        CHECK (id = 1),
    CONSTRAINT ck_configuracion_tienda_moneda
        CHECK (moneda IN ('CLP', 'USD', 'EUR'))
);