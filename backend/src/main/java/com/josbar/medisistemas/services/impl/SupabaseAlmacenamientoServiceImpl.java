package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.exceptions.AlmacenamientoException;
import com.josbar.medisistemas.services.AlmacenamientoService;
import com.josbar.medisistemas.services.ArchivoAlmacenado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Almacena los archivos clínicos en Supabase Storage. El bucket es privado: los archivos
 * se leen mediante enlaces temporales firmados generados por el Backend.
 * Las rutas usan solo caracteres seguros para no depender de codificación de URL.
 */
@Service
public class SupabaseAlmacenamientoServiceImpl implements AlmacenamientoService {

    private static final Logger log = LoggerFactory.getLogger(SupabaseAlmacenamientoServiceImpl.class);
    private static final int SEGUNDOS_VIGENCIA_ENLACE = 300;
    private static final int TAMANIO_PAGINA = 100;

    private final Environment environment;
    private final String baseUrl;
    private final String bucket;
    private final RestClient restClient;

    public SupabaseAlmacenamientoServiceImpl(Environment environment,
                                             @Value("${app.supabase.url:}") String url,
                                             @Value("${app.supabase.service-key:}") String serviceKey,
                                             @Value("${app.supabase.bucket:documentos-clinicos}") String bucket) {
        this.environment = environment;
        this.baseUrl = url.isBlank() || serviceKey.isBlank() ? "" : url.replaceAll("/+$", "") + "/storage/v1";
        this.bucket = bucket;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5_000);
        requestFactory.setReadTimeout(30_000);
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .defaultHeader("Authorization", "Bearer " + serviceKey)
                .defaultHeader("apikey", serviceKey)
                .build();
    }

    @Override
    public String guardar(Integer idPaciente, MultipartFile archivo) {
        exigirConfiguracion();
        String ruta = "paciente-" + idPaciente + "/" + UUID.randomUUID() + "-" + nombreSeguro(archivo.getOriginalFilename());
        MediaType tipo = archivo.getContentType() != null
                ? MediaType.parseMediaType(archivo.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;

        try {
            restClient.post()
                    .uri(baseUrl + "/object/" + bucket + "/" + ruta)
                    .contentType(tipo)
                    .body(archivo.getBytes())
                    .retrieve()
                    .toBodilessEntity();
            return ruta;
        } catch (RestClientException | IOException e) {
            throw new AlmacenamientoException("No se pudo guardar el archivo en el almacenamiento.", e);
        }
    }

    @Override
    public void eliminar(String ruta) {
        try {
            restClient.delete()
                    .uri(baseUrl + "/object/" + bucket + "/" + ruta)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.warn("No se pudo eliminar el archivo huérfano '{}' del almacenamiento: {}", ruta, e.getMessage());
        }
    }

    /**
     * Recorre el bucket: la raíz contiene carpetas (una por paciente, sin id) y dentro de cada una los archivos.
     * Si algo falla se lanza AlmacenamientoException: la limpieza no debe actuar con una lista incompleta.
     */
    @Override
    public List<ArchivoAlmacenado> listarArchivos() {
        exigirConfiguracion();
        List<ArchivoAlmacenado> archivos = new ArrayList<>();
        for (Map<String, Object> entrada : listarEntradas("")) {
            if (entrada.get("id") == null) {
                String carpeta = (String) entrada.get("name");
                for (Map<String, Object> archivo : listarEntradas(carpeta)) {
                    if (archivo.get("id") != null) {
                        archivos.add(aArchivo(carpeta + "/", archivo));
                    }
                }
            } else {
                archivos.add(aArchivo("", entrada));
            }
        }
        return archivos;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> listarEntradas(String prefijo) {
        List<Map<String, Object>> entradas = new ArrayList<>();
        try {
            for (int desplazamiento = 0; ; desplazamiento += TAMANIO_PAGINA) {
                List<Map<String, Object>> pagina = restClient.post()
                        .uri(baseUrl + "/object/list/" + bucket)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("prefix", prefijo, "limit", TAMANIO_PAGINA, "offset", desplazamiento))
                        .retrieve()
                        .body(List.class);
                if (pagina == null || pagina.isEmpty()) {
                    return entradas;
                }
                entradas.addAll(pagina);
                if (pagina.size() < TAMANIO_PAGINA) {
                    return entradas;
                }
            }
        } catch (RestClientException e) {
            throw new AlmacenamientoException("No se pudo listar el almacenamiento.", e);
        }
    }

    private ArchivoAlmacenado aArchivo(String carpeta, Map<String, Object> entrada) {
        Object creado = entrada.get("created_at");
        return new ArchivoAlmacenado(carpeta + entrada.get("name"), creado == null ? Instant.now() : Instant.parse(creado.toString()));
    }

    /** En producción la falta de configuración es un error visible en los logs; en desarrollo es esperable. */
    @EventListener(ApplicationReadyEvent.class)
    void verificarConfiguracion() {
        if (!baseUrl.isEmpty()) {
            return;
        }
        String mensaje = "Supabase Storage sin configurar (SUPABASE_URL / SUPABASE_SERVICE_KEY): "
                + "subir y leer documentos responderá 503.";
        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            log.error(mensaje);
        } else {
            log.info(mensaje);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String generarEnlaceTemporal(String ruta) {
        exigirConfiguracion();
        try {
            Map<String, String> respuesta = restClient.post()
                    .uri(baseUrl + "/object/sign/" + bucket + "/" + ruta)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("expiresIn", SEGUNDOS_VIGENCIA_ENLACE))
                    .retrieve()
                    .body(Map.class);
            if (respuesta == null || respuesta.get("signedURL") == null) {
                throw new AlmacenamientoException("El almacenamiento no devolvió un enlace de lectura.");
            }
            return baseUrl + respuesta.get("signedURL");
        } catch (RestClientException e) {
            throw new AlmacenamientoException("No se pudo generar el enlace del archivo.", e);
        }
    }

    private void exigirConfiguracion() {
        if (baseUrl.isEmpty()) {
            throw new AlmacenamientoException("Falta configurar SUPABASE_URL y SUPABASE_SERVICE_KEY.");
        }
    }

    private String nombreSeguro(String nombreOriginal) {
        String nombre = nombreOriginal == null ? "archivo" : nombreOriginal;
        return nombre.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
