package com.josbar.medisistemas.exceptions;

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Traduce un error de PostgreSQL (violación de unicidad, clave foránea, campo obligatorio, longitud...) a un
 * mensaje que explica la causa real al usuario. No expone SQL ni nombres internos de tablas.
 * Si la causa no es reconocible se devuelve el mensaje genérico.
 */
public final class MensajeIntegridadDatos {

    public record ErrorDeIntegridad(HttpStatus status, String mensaje) {
    }

    private static final String SQLSTATE_CHECK = "23514";
    private static final String SQLSTATE_NULO = "23502";
    private static final String SQLSTATE_FK = "23503";
    private static final String SQLSTATE_UNICO = "23505";
    private static final String SQLSTATE_LONGITUD = "22001";
    private static final String SQLSTATE_RANGO_NUMERICO = "22003";

    private static final String GENERICO = "No se pudo guardar la información porque viola una restricción de la base de datos.";

    /** Restricciones únicas conocidas; el %s es el valor duplicado. */
    private static final Map<String, String> UNICAS = Map.of(
            "Usuario_correo_key", "Ya existe un usuario registrado con el correo electrónico %s.",
            "Usuario_telefono_key", "Ya existe un usuario registrado con el número de teléfono %s.",
            "Paciente_dpi_key", "Ya existe un paciente registrado con el DPI %s.",
            "Consulta_id_cita_key", "La cita ya tiene una consulta registrada.",
            "Medico_pkey", "El usuario ya está registrado como médico."
    );

    /** Tabla referenciada -> el registro con su artículo, para "<X> (id N) no existe". */
    private static final Map<String, String> REGISTROS_REFERENCIADOS = Map.ofEntries(
            Map.entry("Rol", "El rol indicado"),
            Map.entry("Usuario", "El usuario indicado"),
            Map.entry("Especialidad", "La especialidad indicada"),
            Map.entry("Medico", "El médico indicado"),
            Map.entry("Paciente", "El paciente indicado"),
            Map.entry("Cita", "La cita indicada"),
            Map.entry("EstadoCita", "El estado de la cita indicado"),
            Map.entry("Consulta", "La consulta indicada"),
            Map.entry("Documento", "El documento indicado"),
            Map.entry("CategoriaDocumento", "La categoría de documento indicada"),
            Map.entry("DiaSemana", "El día de la semana indicado"),
            Map.entry("MotivoModificacionConsulta", "El motivo de modificación indicado"),
            Map.entry("MotivoModificacionDocumento", "El motivo de modificación indicado")
    );

    /** Tabla que referencia -> cómo nombrarla en plural ("está siendo utilizado por: X"). */
    private static final Map<String, String> TABLAS_PLURAL = Map.ofEntries(
            Map.entry("Usuario", "usuarios"),
            Map.entry("Medico", "médicos"),
            Map.entry("Paciente", "pacientes"),
            Map.entry("Cita", "citas"),
            Map.entry("Consulta", "consultas"),
            Map.entry("SignosVitales", "signos vitales"),
            Map.entry("Documento", "documentos"),
            Map.entry("JornadaMedica", "jornadas médicas"),
            Map.entry("AuditoriaConsulta", "registros de auditoría de consultas"),
            Map.entry("AuditoriaDocumento", "registros de auditoría de documentos")
    );

    private static final Map<String, String> COLUMNAS = Map.of(
            "correo", "correo electrónico",
            "telefono", "teléfono",
            "contrasenia", "contraseña",
            "dpi", "DPI",
            "direccion", "dirección",
            "colegiado", "número de colegiado"
    );

    private static final Pattern CLAVE_DUPLICADA = Pattern.compile("Key \\((.+?)\\)=\\((.*?)\\) already exists");
    private static final Pattern CLAVE_AUSENTE = Pattern.compile("Key \\((.+?)\\)=\\((.*?)\\) is not present in table \"(.+?)\"");
    private static final Pattern CLAVE_EN_USO = Pattern.compile("is still referenced from table \"(.+?)\"");
    private static final Pattern LONGITUD = Pattern.compile("\\((\\d+)\\)");

    private MensajeIntegridadDatos() {
    }

    public static ErrorDeIntegridad traducir(Throwable error) {
        PSQLException psql = buscarPSQLException(error);
        if (psql == null || psql.getSQLState() == null) {
            return generico();
        }
        ServerErrorMessage servidor = psql.getServerErrorMessage();
        String detalle = servidor != null && servidor.getDetail() != null ? servidor.getDetail() : "";
        String constraint = servidor != null ? servidor.getConstraint() : null;
        String columna = servidor != null ? servidor.getColumn() : null;

        return switch (psql.getSQLState()) {
            case SQLSTATE_UNICO -> unico(constraint, detalle);
            case SQLSTATE_FK -> claveForanea(detalle);
            case SQLSTATE_CHECK -> new ErrorDeIntegridad(HttpStatus.BAD_REQUEST,
                    servidor != null && servidor.getMessage() != null ? servidor.getMessage()
                            : "El valor no cumple una regla de la base de datos.");
            case SQLSTATE_NULO -> new ErrorDeIntegridad(HttpStatus.BAD_REQUEST,
                    "El campo «" + etiqueta(columna) + "» es obligatorio.");
            case SQLSTATE_LONGITUD -> longitud(psql.getMessage());
            case SQLSTATE_RANGO_NUMERICO -> new ErrorDeIntegridad(HttpStatus.BAD_REQUEST,
                    "Uno de los valores numéricos está fuera del rango permitido.");
            default -> generico();
        };
    }

    private static ErrorDeIntegridad unico(String constraint, String detalle) {
        Matcher m = CLAVE_DUPLICADA.matcher(detalle);
        String columna = m.find() ? m.group(1) : null;
        String valor = columna != null ? m.group(2) : "";
        String plantilla = constraint != null ? UNICAS.get(constraint) : null;
        String mensaje = plantilla != null
                ? String.format(plantilla, "«" + valor + "»")
                : columna != null
                    ? "Ya existe un registro con el mismo valor en «" + etiqueta(columna) + "»: «" + valor + "»."
                    : "Ya existe un registro con los mismos datos.";
        return new ErrorDeIntegridad(HttpStatus.CONFLICT, mensaje);
    }

    private static ErrorDeIntegridad claveForanea(String detalle) {
        Matcher ausente = CLAVE_AUSENTE.matcher(detalle);
        if (ausente.find()) {
            String entidad = REGISTROS_REFERENCIADOS.getOrDefault(ausente.group(3), "El registro indicado");
            return new ErrorDeIntegridad(HttpStatus.BAD_REQUEST, entidad + " (id " + ausente.group(2) + ") no existe.");
        }
        Matcher enUso = CLAVE_EN_USO.matcher(detalle);
        if (enUso.find()) {
            String usuarios = TABLAS_PLURAL.getOrDefault(enUso.group(1), "otros registros");
            return new ErrorDeIntegridad(HttpStatus.CONFLICT,
                    "No se puede eliminar o modificar el registro porque está siendo utilizado por: " + usuarios + ".");
        }
        return new ErrorDeIntegridad(HttpStatus.BAD_REQUEST, "Hace referencia a un registro que no existe o que está en uso.");
    }

    private static ErrorDeIntegridad longitud(String mensajePostgres) {
        Matcher m = LONGITUD.matcher(mensajePostgres == null ? "" : mensajePostgres);
        String limite = m.find() ? " (máximo " + m.group(1) + " caracteres)" : "";
        return new ErrorDeIntegridad(HttpStatus.BAD_REQUEST, "Uno de los textos supera la longitud máxima permitida" + limite + ".");
    }

    private static ErrorDeIntegridad generico() {
        return new ErrorDeIntegridad(HttpStatus.BAD_REQUEST, GENERICO);
    }

    /** Nombre legible de una columna en snake_case ("primer_nombre" -> "primer nombre"). */
    public static String etiqueta(String columna) {
        if (columna == null) {
            return "requerido";
        }
        String legible = COLUMNAS.get(columna);
        if (legible != null) {
            return legible;
        }
        return columna.replaceFirst("^id_", "").replace('_', ' ');
    }

    private static PSQLException buscarPSQLException(Throwable error) {
        for (Throwable actual = error; actual != null; actual = actual.getCause() == actual ? null : actual.getCause()) {
            if (actual instanceof PSQLException psql) {
                return psql;
            }
        }
        return null;
    }
}
