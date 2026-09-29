package com.josbar.medisistemas.services.impl;

import com.josbar.medisistemas.services.CatalogoService;
import com.josbar.medisistemas.domain.dtos.catalogo.CatalogoResponseDTO;
import com.josbar.medisistemas.mappers.impl.CatalogoMapper;
import com.josbar.medisistemas.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CatalogoServiceImpl implements CatalogoService {

    private final RolRepository rolRepository;
    private final DiaSemanaRepository diaSemanaRepository;
    private final EstadoCitaRepository estadoCitaRepository;
    private final CategoriaDocumentoRepository categoriaDocumentoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final MotivoModificacionConsultaRepository motivoModificacionConsultaRepository;
    private final MotivoModificacionDocumentoRepository motivoModificacionDocumentoRepository;
    private final CatalogoMapper catalogoMapper;

    public CatalogoServiceImpl(RolRepository rolRepository, DiaSemanaRepository diaSemanaRepository,
                                EstadoCitaRepository estadoCitaRepository, CategoriaDocumentoRepository categoriaDocumentoRepository,
                                EspecialidadRepository especialidadRepository, MotivoModificacionConsultaRepository motivoModificacionConsultaRepository,
                                MotivoModificacionDocumentoRepository motivoModificacionDocumentoRepository, CatalogoMapper catalogoMapper) {
        this.rolRepository = rolRepository;
        this.diaSemanaRepository = diaSemanaRepository;
        this.estadoCitaRepository = estadoCitaRepository;
        this.categoriaDocumentoRepository = categoriaDocumentoRepository;
        this.especialidadRepository = especialidadRepository;
        this.motivoModificacionConsultaRepository = motivoModificacionConsultaRepository;
        this.motivoModificacionDocumentoRepository = motivoModificacionDocumentoRepository;
        this.catalogoMapper = catalogoMapper;
    }

    @Override
    public List<CatalogoResponseDTO> listarRoles() {
        return toList(rolRepository.findAll());
    }

    @Override
    public List<CatalogoResponseDTO> listarDiasSemana() {
        return toList(diaSemanaRepository.findAll());
    }

    @Override
    public List<CatalogoResponseDTO> listarEstadosCita() {
        return toList(estadoCitaRepository.findAll());
    }

    @Override
    public List<CatalogoResponseDTO> listarCategoriasDocumento() {
        return toList(categoriaDocumentoRepository.findAll());
    }

    @Override
    public List<CatalogoResponseDTO> listarEspecialidades() {
        return toList(especialidadRepository.findAll());
    }

    @Override
    public List<CatalogoResponseDTO> listarMotivosConsulta() {
        return toList(motivoModificacionConsultaRepository.findAll());
    }

    @Override
    public List<CatalogoResponseDTO> listarMotivosDocumento() {
        return toList(motivoModificacionDocumentoRepository.findAll());
    }

    private List<CatalogoResponseDTO> toList(Iterable<?> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(catalogoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
