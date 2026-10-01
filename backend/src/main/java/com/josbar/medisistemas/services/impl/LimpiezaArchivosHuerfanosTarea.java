package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.repositories.AuditoriaDocumentoRepository;
import com.josbar.medisistemas.repositories.DocumentoRepository;
import com.josbar.medisistemas.services.AlmacenamientoService;
import com.josbar.medisistemas.services.ArchivoAlmacenado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Elimina del almacenamiento los archivos que ningún documento ni auditoría referencia. Las transacciones de
 * {@link DocumentoServiceImpl} evitan los huérfanos cuando la operación falla, pero Storage no es transaccional:
 * si el proceso se cae entre la subida y la confirmación en la base, el archivo queda sin dueño.
 * <p>
 * Solo corre con el perfil "prod": una base de desarrollo no conoce los archivos de otra y los borraría.
 * Un archivo recién subido puede estar en medio de su transacción, por eso solo se borran los que superan
 * el período de gracia. Los archivos reemplazados NO son huérfanos: la auditoría los referencia.
 */
@Component
@Profile("prod")
public class LimpiezaArchivosHuerfanosTarea {

    private static final Logger log = LoggerFactory.getLogger(LimpiezaArchivosHuerfanosTarea.class);

    private final AlmacenamientoService almacenamientoService;
    private final DocumentoRepository documentoRepository;
    private final AuditoriaDocumentoRepository auditoriaRepository;
    private final Duration gracia;

    public LimpiezaArchivosHuerfanosTarea(AlmacenamientoService almacenamientoService,
                                          DocumentoRepository documentoRepository,
                                          AuditoriaDocumentoRepository auditoriaRepository,
                                          @Value("${app.almacenamiento.limpieza.gracia-minutos:1440}") long graciaMinutos) {
        this.almacenamientoService = almacenamientoService;
        this.documentoRepository = documentoRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.gracia = Duration.ofMinutes(graciaMinutos);
    }

    @Scheduled(initialDelayString = "PT${app.almacenamiento.limpieza.intervalo-minutos:60}M",
            fixedDelayString = "PT${app.almacenamiento.limpieza.intervalo-minutos:60}M")
    public void limpiar() {
        try {
            List<ArchivoAlmacenado> archivos = almacenamientoService.listarArchivos();
            Set<String> referenciadas = rutasReferenciadas();
            Instant limite = Instant.now().minus(gracia);
            int eliminados = 0;
            for (ArchivoAlmacenado archivo : archivos) {
                if (!referenciadas.contains(archivo.ruta()) && archivo.creado().isBefore(limite)) {
                    log.warn("Eliminando archivo huérfano del almacenamiento: {}", archivo.ruta());
                    almacenamientoService.eliminar(archivo.ruta());
                    eliminados++;
                }
            }
            log.info("Limpieza de archivos huérfanos: {} archivos revisados, {} eliminados.", archivos.size(), eliminados);
        } catch (RuntimeException e) {
            log.error("No se pudo completar la limpieza de archivos huérfanos: {}", e.getMessage());
        }
    }

    private Set<String> rutasReferenciadas() {
        Set<String> rutas = new HashSet<>(documentoRepository.findAllUrls());
        rutas.addAll(auditoriaRepository.findAllUrlsAnteriores());
        rutas.addAll(auditoriaRepository.findAllUrlsNuevas());
        return rutas;
    }
}
