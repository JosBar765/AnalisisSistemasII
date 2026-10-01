package com.josbar.medisistemas.services;

import java.time.Instant;

/** Archivo existente en el almacenamiento: su ruta (la misma que guarda Documento.url) y cuándo se creó. */
public record ArchivoAlmacenado(String ruta, Instant creado) {
}
