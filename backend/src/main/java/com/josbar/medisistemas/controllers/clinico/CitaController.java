package com.josbar.medisistemas.controllers.clinico;

import com.josbar.medisistemas.domain.dtos.cita.CitaRequestDTO;
import com.josbar.medisistemas.domain.dtos.cita.CitaResponseDTO;
import com.josbar.medisistemas.domain.dtos.cita.HorarioDisponibleResponseDTO;
import com.josbar.medisistemas.mappers.impl.CitaMapper;
import com.josbar.medisistemas.services.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;
    private final CitaMapper citaMapper;

    public CitaController(CitaService citaService, CitaMapper citaMapper) {
        this.citaService = citaService;
        this.citaMapper = citaMapper;
    }

    @PostMapping
    public ResponseEntity<CitaResponseDTO> programarCita(@Valid @RequestBody CitaRequestDTO request) {
        var saved = citaService.programar(citaMapper.toEntity(request));
        return new ResponseEntity<>(citaMapper.toResponse(saved), HttpStatus.CREATED);
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<List<HorarioDisponibleResponseDTO>> consultarHorarios(
            @RequestParam Integer idMedico,
            @RequestParam LocalDate fecha) {
        List<HorarioDisponibleResponseDTO> disponibilidad = citaService.obtenerHorariosDisponibles(idMedico, fecha);
        return new ResponseEntity<>(disponibilidad, HttpStatus.OK);
    }

    @GetMapping("/agenda-diaria")
    public ResponseEntity<List<CitaResponseDTO>> consultarAgendaDiaria(@RequestParam LocalDate fecha) {
        List<CitaResponseDTO> agenda = citaService.obtenerAgendaDiaria(fecha).stream()
                .map(citaMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(agenda, HttpStatus.OK);
    }

    @GetMapping("/agenda")
    public ResponseEntity<List<CitaResponseDTO>> consultarAgendaPorRango(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {
        List<CitaResponseDTO> agenda = citaService.obtenerAgendaPorRango(desde, hasta).stream()
                .map(citaMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(agenda, HttpStatus.OK);
    }

    @GetMapping("/agenda-medico")
    public ResponseEntity<List<CitaResponseDTO>> consultarAgendaMedico(
            @RequestParam Integer idMedico,
            @RequestParam LocalDate fecha) {
        List<CitaResponseDTO> agenda = citaService.obtenerAgendaPorMedico(idMedico, fecha).stream()
                .map(citaMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(agenda, HttpStatus.OK);
    }

    @GetMapping("/mis-citas")
    public ResponseEntity<List<CitaResponseDTO>> consultarMisCitas(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta,
            @AuthenticationPrincipal Jwt jwt) {
        List<CitaResponseDTO> agenda = citaService.obtenerAgendaDelMedico(Integer.valueOf(jwt.getSubject()), desde, hasta).stream()
                .map(citaMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(agenda, HttpStatus.OK);
    }

    @GetMapping("/mis-citas/{id}")
    public ResponseEntity<CitaResponseDTO> consultarMiCita(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal Jwt jwt) {
        var cita = citaService.obtenerCitaDelMedico(id, Integer.valueOf(jwt.getSubject()));
        return new ResponseEntity<>(citaMapper.toResponse(cita), HttpStatus.OK);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<CitaResponseDTO> cancelarCita(@PathVariable("id") Integer id) {
        var canceled = citaService.cancelar(id);
        return new ResponseEntity<>(citaMapper.toResponse(canceled), HttpStatus.OK);
    }

    @PutMapping("/{id}/reprogramar")
    public ResponseEntity<CitaResponseDTO> reprogramarCita(
            @PathVariable("id") Integer id,
            @Valid @RequestBody CitaRequestDTO request) {
        var reprogrammed = citaService.reprogramar(id, citaMapper.toEntity(request));
        return new ResponseEntity<>(citaMapper.toResponse(reprogrammed), HttpStatus.OK);
    }

    @PatchMapping("/{id}/llegada")
    public ResponseEntity<CitaResponseDTO> registrarLlegada(@PathVariable("id") Integer id) {
        var cita = citaService.registrarLlegada(id);
        return new ResponseEntity<>(citaMapper.toResponse(cita), HttpStatus.OK);
    }

    @PatchMapping("/{id}/llamado")
    public ResponseEntity<CitaResponseDTO> solicitarLlamado(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal Jwt jwt) {
        var cita = citaService.solicitarLlamado(id, Integer.valueOf(jwt.getSubject()));
        return new ResponseEntity<>(citaMapper.toResponse(cita), HttpStatus.OK);
    }
}
