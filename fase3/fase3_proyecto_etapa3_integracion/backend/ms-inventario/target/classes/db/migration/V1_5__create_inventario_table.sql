CREATE SCHEMA IF NOT EXISTS bd_inventario;

CREATE TABLE IF NOT EXISTS bd_inventario.inventario (
    id_inventario BIGSERIAL PRIMARY KEY,    
    id_producto BIGINT NOT NULL,         
    stock_actual INT NOT NULL DEFAULT 0, 
    stock_minimo INT NOT NULL DEFAULT 0,
    fecha_vencimiento DATE              
);

