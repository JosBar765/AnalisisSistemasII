-- Datos iniciales de MediSistema (roles, dias, estados, especialidades y usuario administrador).
-- Se ejecuta automaticamente por el contenedor de Postgres despues de 01_schema.sql.

INSERT INTO "Rol" (rol) VALUES
  ('ADMINISTRADOR'),
  ('SECRETARIA'),
  ('MEDICO');

INSERT INTO "DiaSemana" (dia_semana) VALUES
  ('Lunes'),
  ('Martes'),
  ('Miercoles'),
  ('Jueves'),
  ('Viernes'),
  ('Sabado'),
  ('Domingo');

INSERT INTO "EstadoCita" (estado_cita) VALUES
  ('En espera'),
  ('Atendido'),
  ('Cancelado');

INSERT INTO "Especialidad" (especialidad) VALUES
  ('Medicina General'),
  ('Pediatria'),
  ('Ginecologia'),
  ('Cardiologia'),
  ('Dermatologia');

INSERT INTO "CategoriaDocumento" (categoria_documento) VALUES
  ('Examen de Laboratorio'),
  ('Radiografia'),
  ('Resonancia'),
  ('Receta'),
  ('Referencia Medica'),
  ('Otro');

INSERT INTO "MotivoModificacionConsulta" (motivo_modificacion, detalle_motivo) VALUES
  ('Correccion de error', 'Correccion de datos ingresados incorrectamente'),
  ('Informacion adicional', 'Se agrega informacion clinica adicional');

INSERT INTO "MotivoModificacionDocumento" (motivo_modificacion, detalle_motivo) VALUES
  ('Archivo incorrecto', 'El archivo cargado no correspondia al documento'),
  ('Actualizacion de version', 'Se reemplaza por una version mas reciente del documento');

-- Usuario administrador inicial
-- correo: admin@medisistema.com
-- contrasena: Admin123*  (hash BCrypt, compatible con Spring Security BCryptPasswordEncoder)
INSERT INTO "Usuario" (id_rol, primer_nombre, primer_apellido, segundo_apellido, correo, telefono, contrasenia, fecha_creacion)
VALUES (
  (SELECT id FROM "Rol" WHERE rol = 'ADMINISTRADOR'),
  'Administrador',
  'Sistema',
  'MediSistema',
  'admin@medisistema.com',
  '00000000',
  '$2b$10$Y8g3My9pkNLV/6ezWPqPu.v8w.Hl876kxQqFlrg2FWyjPkabzVyhO',
  now()
);
