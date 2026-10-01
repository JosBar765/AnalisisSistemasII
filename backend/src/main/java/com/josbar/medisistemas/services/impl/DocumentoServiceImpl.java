package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.AlmacenamientoService;
import com.josbar.medisistemas.services.DocumentoService;
import com.josbar.medisistemas.domain.dtos.documento.ActualizarDocumentoRequestDTO;
import com.josbar.medisistemas.domain.entities.AuditoriaDocumentoEntity;
import com.josbar.medisistemas.domain.entities.CategoriaDocumentoEntity;
import com.josbar.medisistemas.domain.entities.DocumentoEntity;
import com.josbar.medisistemas.domain.entities.MotivoModificacionDocumentoEntity;
import com.josbar.medisistemas.domain.entities.PacienteEntity;
import com.josbar.medisistemas.domain.entities.UsuarioEntity;
import com.josbar.medisistemas.exceptions.BusinessRuleException;
import com.josbar.medisistemas.exceptions.ResourceNotFoundException;
import com.josbar.medisistemas.repositories.AuditoriaDocumentoRepository;
import com.josbar.medisistemas.repositories.CategoriaDocumentoRepository;
import com.josbar.medisistemas.repositories.DocumentoRepository;
import com.josbar.medisistemas.repositories.MotivoModificacionDocumentoRepository;
import com.josbar.medisistemas.repositories.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Gestión de documentos clínicos (UC-SEC-002). Los documentos pertenecen al paciente.
 * Al reemplazar el archivo no se elimina el anterior del almacenamiento: solo se deja de
 * usar como referencia activa y se registra la auditoría de la actualización.
 */
@Service
public class DocumentoServiceImpl implements DocumentoService {

    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of("pdf", "jpg", "jpeg", "png", "dcm");

    private final DocumentoRepository documentoRepository;
    private final PacienteRepository pacienteRepository;
    private final CategoriaDocumentoRepository categoriaDocumentoRepository;
    private final MotivoModificacionDocumentoRepository motivoRepository;
    private final AuditoriaDocumentoRepository auditoriaRepository;
    private final AlmacenamientoService almacenamientoService;

    public DocumentoServiceImpl(DocumentoRepository documentoRepository, PacienteRepository pacienteRepository,
                                CategoriaDocumentoRepository categoriaDocumentoRepository,
                                MotivoModificacionDocumentoRepository motivoRepository,
                                AuditoriaDocumentoRepository auditoriaRepository,
                                AlmacenamientoService almacenamientoService) {
        this.documentoRepository = documentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.categoriaDocumentoRepository = categoriaDocumentoRepository;
        this.motivoRepository = motivoRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.almacenamientoService = almacenamientoService;
    }

    @Override
    @Transactional
    public DocumentoEntity subir(DocumentoEntity entity, MultipartFile archivo, Integer idUsuarioCarga) {
        validarArchivo(archivo);
        PacienteEntity paciente = pacienteRepository.findById(entity.getPacienteEntity().getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente indicado."));
        CategoriaDocumentoEntity categoria = findCategoria(entity.getCategoriaDocumentoEntity().getId());

        UsuarioEntity usuarioCarga = new UsuarioEntity();
        usuarioCarga.setId(idUsuarioCarga);

        entity.setPacienteEntity(paciente);
        entity.setCategoriaDocumentoEntity(categoria);
        entity.setUsuarioEntityCarga(usuarioCarga);
        entity.setNombre(archivo.getOriginalFilename());
        entity.setUrl(guardarArchivo(paciente.getId(), archivo));
        entity.setFechaCarga(LocalDateTime.now());
        return documentoRepository.save(entity);
    }

    @Override
    @Transactional
    public DocumentoEntity actualizar(Integer id, ActualizarDocumentoRequestDTO request, MultipartFile archivo, Integer idUsuario) {
        validarArchivo(archivo);
        DocumentoEntity entity = findById(id);
        MotivoModificacionDocumentoEntity motivo = motivoRepository.findById(request.getIdMotivoModificacion())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el motivo de modificación indicado."));

        String nombreAnterior = entity.getNombre();
        String urlAnterior = entity.getUrl();

        if (request.getIdCategoriaDocumento() != null) {
            entity.setCategoriaDocumentoEntity(findCategoria(request.getIdCategoriaDocumento()));
        }
        entity.setNombre(archivo.getOriginalFilename());
        entity.setUrl(guardarArchivo(entity.getPacienteEntity().getId(), archivo));
        DocumentoEntity actualizado = documentoRepository.save(entity);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(idUsuario);
        auditoriaRepository.save(AuditoriaDocumentoEntity.builder()
                .documentoEntity(actualizado)
                .nombreAnterior(nombreAnterior)
                .urlAnterior(urlAnterior)
                .usuarioEntity(usuario)
                .fechaModificacion(LocalDateTime.now())
                .motivoModificacionDocumentoEntity(motivo)
                .nombreNuevo(actualizado.getNombre())
                .urlNuevo(actualizado.getUrl())
                .build());
        return actualizado;
    }

    @Override
    public List<DocumentoEntity> listarPorPaciente(Integer idPaciente) {
        return documentoRepository.findByPacienteEntityId(idPaciente);
    }

    @Override
    public String obtenerEnlaceLectura(Integer id) {
        return almacenamientoService.generarEnlaceTemporal(findById(id).getUrl());
    }

    /**
     * Guarda el archivo nuevo y, si la transacción no se confirma, lo elimina para no dejar huérfanos.
     * Solo se borra el archivo recién subido: el anterior nunca se elimina.
     */
    private String guardarArchivo(Integer idPaciente, MultipartFile archivo) {
        String ruta = almacenamientoService.guardar(idPaciente, archivo);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    almacenamientoService.eliminar(ruta);
                }
            }
        });
        return ruta;
    }

    private DocumentoEntity findById(Integer id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el documento con id " + id));
    }

    private CategoriaDocumentoEntity findCategoria(Integer id) {
        return categoriaDocumentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría de documento indicada."));
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessRuleException("Debe seleccionar un archivo.");
        }
        String nombre = archivo.getOriginalFilename() == null ? "" : archivo.getOriginalFilename();
        String extension = nombre.contains(".") ? nombre.substring(nombre.lastIndexOf('.') + 1).toLowerCase() : "";
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new BusinessRuleException("Tipo de archivo no permitido. Formatos válidos: PDF, JPG, PNG y DICOM (.dcm).");
        }
    }
}
