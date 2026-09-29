package com.josbar.medisistemas.controllers.clinico;

import com.josbar.medisistemas.domain.dtos.cita.CitaRequestDTO;
import com.josbar.medisistemas.domain.dtos.cita.CitaResponseDTO;
import com.josbar.medisistemas.domain.dtos.cita.HorarioDisponibleResponseDTO;
import com.josbar.medisistemas.mappers.impl.CitaMapper;
import com.josbar.medisistemas.services.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CitaResponseDTO> programarCita(@RequestBody CitaRequestDTO request) {
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

    @GetMapping("/agenda-medico")
    public ResponseEntity<List<CitaResponseDTO>> consultarAgendaMedico(
            @RequestParam Integer idMedico,
            @RequestParam LocalDate fecha) {
        List<CitaResponseDTO> agenda = citaService.obtenerAgendaPorMedico(idMedico, fecha).stream()
                .map(citaMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(agenda, HttpStatus.OK);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<CitaResponseDTO> cancelarCita(@PathVariable("id") Integer id) {
        var canceled = citaService.cancelar(id);
        return new ResponseEntity<>(citaMapper.toResponse(canceled), HttpStatus.OK);
    }

    @PutMapping("/{id}/reprogramar")
    public ResponseEntity<CitaResponseDTO> reprogramarCita(
            @PathVariable("id") Integer id,
            @RequestBody CitaRequestDTO request) {
        var reprogrammed = citaService.reprogramar(id, citaMapper.toEntity(request));
        return new ResponseEntity<>(citaMapper.toResponse(reprogrammed), HttpStatus.OK);
    }
}