package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.exceptions.AlmacenamientoException;
import com.josbar.medisistemas.services.AlmacenamientoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Almacena los archivos clínicos en Supabase Storage. El bucket es privado: los archivos
 * se leen mediante enlaces temporales firmados generados por el Backend.
 * Las rutas usan solo caracteres seguros para no depender de codificación de URL.
 */
@Service
public class SupabaseAlmacenamientoServiceImpl implements AlmacenamientoService {

    private static final int SEGUNDOS_VIGENCIA_ENLACE = 300;

    private final String baseUrl;
    private final String bucket;
    private final RestClient restClient;

    public SupabaseAlmacenamientoServiceImpl(@Value("${app.supabase.url:}") String url,
                                             @Value("${app.supabase.service-key:}") String serviceKey,
                                             @Value("${app.supabase.bucket:documentos-clinicos}") String bucket) {
        this.baseUrl = url.isBlank() ? "" : url.replaceAll("/+$", "") + "/storage/v1";
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
