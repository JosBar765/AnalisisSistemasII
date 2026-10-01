package com.josbar.medisistemas.domain.dtos.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Los campos obligatorios al crear se exigen en {@code UsuarioServiceImpl.save} (el PUT admite actualizaciones
 * parciales). Aquí se valida lo que se envíe: no vacío y dentro de la longitud de la columna.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioRequestDTO {
    private Integer idRol;
    @Pattern(regexp = ".*\\S.*", message = "El primer nombre no puede estar vacío.")
    @Size(max = 50, message = "El primer nombre no puede superar 50 caracteres.")
    private String primerNombre;
    @Size(max = 100, message = "El segundo nombre no puede superar 100 caracteres.")
    private String segundoNombre;
    @Pattern(regexp = ".*\\S.*", message = "El primer apellido no puede estar vacío.")
    @Size(max = 50, message = "El primer apellido no puede superar 50 caracteres.")
    private String primerApellido;
    @Pattern(regexp = ".*\\S.*", message = "El segundo apellido no puede estar vacío.")
    @Size(max = 50, message = "El segundo apellido no puede superar 50 caracteres.")
    private String segundoApellido;
    @Email(message = "El correo electrónico no tiene un formato válido.")
    @Size(max = 255, message = "El correo electrónico no puede superar 255 caracteres.")
    private String correo;
    @Pattern(regexp = ".*\\S.*", message = "El teléfono no puede estar vacío.")
    @Size(max = 15, message = "El teléfono no puede superar 15 caracteres.")
    private String telefono;
    @Size(max = 72, message = "La contraseña no puede superar 72 caracteres.")
    private String contrasenia;
    private Boolean estado; // true para activo, false para inactivo
}
