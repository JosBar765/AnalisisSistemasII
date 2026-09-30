package com.josbar.medisistemas.services;

import org.springframework.web.multipart.MultipartFile;

public interface AlmacenamientoService {

    /** Guarda el archivo en el almacenamiento y devuelve la ruta que lo referencia (columna Documento.url). */
    String guardar(Integer idPaciente, MultipartFile archivo);

    /** Elimina un archivo guardado. Es de mejor esfuerzo: si falla solo se registra, no lanza excepción. */
    void eliminar(String ruta);

    /** Genera un enlace temporal de lectura para una ruta previamente guardada. */
    String generarEnlaceTemporal(String ruta);
}
