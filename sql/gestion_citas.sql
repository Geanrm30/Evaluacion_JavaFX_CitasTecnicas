DROP TABLE IF EXISTS cita_tecnica;
DROP TABLE IF EXISTS cliente;

CREATE TABLE cliente (
                         id        SERIAL PRIMARY KEY,
                         nombres   VARCHAR(80)  NOT NULL,
                         apellidos VARCHAR(80)  NOT NULL,
                         telefono  VARCHAR(20)  NOT NULL,
                         correo    VARCHAR(120) NOT NULL UNIQUE,
                         activo    BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE cita_tecnica (
                              id            SERIAL PRIMARY KEY,
                              cliente_id    INT          NOT NULL,
                              fecha         DATE         NOT NULL,
                              hora          TIME         NOT NULL,
                              tipo_servicio VARCHAR(80)  NOT NULL,
                              descripcion   VARCHAR(255) NOT NULL,
                              estado        VARCHAR(20)  NOT NULL,
                              CONSTRAINT fk_cita_cliente
                                  FOREIGN KEY (cliente_id) REFERENCES cliente(id)
                                      ON DELETE RESTRICT,
                              CONSTRAINT chk_estado
                                  CHECK (estado IN ('Pendiente', 'En proceso', 'Finalizada', 'Cancelada'))
);

CREATE INDEX idx_cita_cliente ON cita_tecnica(cliente_id);
CREATE INDEX idx_cita_fecha   ON cita_tecnica(fecha);
CREATE INDEX idx_cita_estado  ON cita_tecnica(estado);

-- Datos de prueba
INSERT INTO cliente (nombres, apellidos, telefono, correo, activo) VALUES
                                                                       ('Maria Jose', 'Lopez Garcia', '88881111', 'maria.lopez@correo.com', TRUE),
                                                                       ('Carlos Andres', 'Perez Ruiz', '88882222', 'carlos.perez@correo.com', TRUE);

INSERT INTO cita_tecnica (cliente_id, fecha, hora, tipo_servicio, descripcion, estado) VALUES
                                                                                           (1, '2026-10-15', '09:00', 'Reparacion', 'Laptop no enciende', 'Pendiente'),
                                                                                           (2, '2026-10-16', '14:30', 'Mantenimiento', 'Limpieza y formateo de PC', 'En proceso');