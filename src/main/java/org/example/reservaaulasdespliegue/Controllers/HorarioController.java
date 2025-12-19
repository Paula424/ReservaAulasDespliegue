package org.example.reservaaulasdespliegue.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.reservaaulasdespliegue.DTO.HorarioDTO;
import org.example.reservaaulasdespliegue.DTO.HorarioRequest;
import org.example.reservaaulasdespliegue.Entities.Horario;
import org.example.reservaaulasdespliegue.Services.HorarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================
 * CONTROLADOR DE  HORARIOS
 * ============================================
 *
 * Maneja los tramos horarios del centro educativo.
 * Los tramos definen las franjas horarias disponibles para reservar.
 *
 * RUTAS BASE: /tramo-horario
 *
 * PERMISOS:
 * - GET (consultar): ADMIN y PROFESOR
 * - POST (crear): Solo ADMIN
 * - DELETE (eliminar): Solo ADMIN
 */
@RestController
@RequestMapping("/tramo-horario")
@RequiredArgsConstructor
public class HorarioController {

    // Servicio con la lógica de negocio
    private final HorarioService horarioService;

    /**
     * LISTAR TODOS LOS TRAMOS HORARIOS
     *
     * GET /tramo-horario
     *
     * PERMISOS: ADMIN y PROFESOR
     *
     * Devuelve todos los tramos configurados en el sistema.
     * Ejemplo: Lunes 1ª hora, Lunes 2ª hora, Martes 1ª hora, etc.
     *
     * @return Lista de TramoHorarioDTO
     */
    @GetMapping
    public ResponseEntity<List<HorarioDTO>> listar() {
        List<HorarioDTO> tramos = horarioService.listarTodos();
        return ResponseEntity.ok(tramos);
    }

    /**
     * OBTENER UN TRAMO HORARIO ESPECÍFICO
     *
     * GET /tramo-horario/{id}
     *
     * PERMISOS: ADMIN y PROFESOR
     *
     * @param id ID del tramo horario
     * @return TramoHorarioDTO con los datos del tramo
     */
    @GetMapping("/{id}")
    public ResponseEntity<HorarioDTO> obtener(@PathVariable Long id) {
        HorarioDTO tramo = horarioService.obtenerPorId(id);
        return ResponseEntity.ok(tramo);
    }

    /**
     * CREAR NUEVO TRAMO HORARIO
     *
     * POST /tramo-horario
     *
     * PERMISOS: Solo ADMIN
     *
     * Crea un nuevo tramo horario en el sistema.
     * Solo un administrador puede definir el horario del centro.
     *
     * Body JSON ejemplo:
     * {
     *   "diaSemana": "LUNES",
     *   "sesionDia": 3,
     *   "horaInicio": "10:30:00",
     *   "horaFin": "11:25:00",
     *   "tipo": "LECTIVA"
     * }
     *
     * @param request Datos del tramo horario (validado con @Valid)
     * @return TramoHorarioDTO del tramo creado
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede crear tramos
    public ResponseEntity<HorarioDTO> crear(@Valid @RequestBody HorarioRequest request) {
        HorarioDTO tramo = horarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tramo);
    }

    /**
     * ELIMINAR TRAMO HORARIO
     *
     * DELETE /tramo-horario/{id}
     *
     * PERMISOS: Solo ADMIN
     *
     * IMPORTANTE: Si el tramo tiene reservas asociadas, se eliminarán también.
     *
     * @param id ID del tramo a eliminar
     * @return 204 No Content si se eliminó correctamente
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede eliminar tramos
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        horarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * LISTAR TRAMOS DE UN DÍA ESPECÍFICO
     *
     * GET /tramo-horario/dia/{diaSemana}
     *
     * PERMISOS: ADMIN y PROFESOR
     *
     * Devuelve todos los tramos de un día (ej: todos los tramos del LUNES).
     * Útil para mostrar el horario completo de un día.
     *
     * @param diaSemana Día de la semana (LUNES, MARTES, MIERCOLES, JUEVES, VIERNES)
     * @return Lista de TramoHorarioDTO del día
     */
    @GetMapping("/dia/{diaSemana}")
    public ResponseEntity<List<HorarioDTO>> listarPorDia(
            @PathVariable Horario.DiaSemana diaSemana) {

        List<HorarioDTO> tramos = horarioService.listarPorDia(diaSemana);
        return ResponseEntity.ok(tramos);
    }
}