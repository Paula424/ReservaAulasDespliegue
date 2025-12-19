package org.example.reservaaulasdespliegue.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.reservaaulasdespliegue.DTO.ReservaDTO;
import org.example.reservaaulasdespliegue.DTO.ReservaRequest;
import org.example.reservaaulasdespliegue.Services.ReservasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================
 * CONTROLADOR DE RESERVAS
 * ============================================
 *
 * Maneja todos los endpoints relacionados con reservas.
 *
 * RUTAS BASE: /reservas
 *
 * PERMISOS:
 * - GET /reservas (listar todas): Solo ADMIN
 * - GET /reservas/{id}: ADMIN y PROFESOR (el profesor solo ve las suyas)
 * - GET /reservas/mis-reservas: ADMIN y PROFESOR (ver propias reservas)
 * - POST (crear): ADMIN y PROFESOR
 * - DELETE (eliminar): ADMIN (cualquiera) o PROFESOR (solo las suyas)
 */
@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    // Servicio con la lógica de negocio
    private final ReservasService reservaService;

    /**
     * LISTAR TODAS LAS RESERVAS
     *
     * GET /reservas
     *
     * PERMISOS: Solo ADMIN
     * Los profesores no pueden ver todas las reservas, solo las suyas.
     *
     * @return Lista de ReservaDTO con todas las reservas del sistema
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede ver todas las reservas
    public ResponseEntity<List<ReservaDTO>> listar() {
        List<ReservaDTO> reservas = reservaService.listarTodas();
        return ResponseEntity.ok(reservas);
    }

    /**
     * OBTENER UNA RESERVA ESPECÍFICA
     *
     * GET /reservas/{id}
     *
     * PERMISOS: ADMIN y PROFESOR
     * (Aunque aquí no validamos si el profesor es dueño,
     * se podría añadir esa validación si se requiere)
     *
     * @param id ID de la reserva
     * @return ReservaDTO con los datos completos de la reserva
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtener(@PathVariable Long id, Authentication authentication) {
        ReservaDTO reserva = reservaService.obtenerPorId(id);
        return ResponseEntity.ok(reserva);
    }

    /**
     * LISTAR MIS RESERVAS (usuario autenticado)
     *
     * GET /reservas/mis-reservas
     *
     * PERMISOS: ADMIN y PROFESOR
     * Devuelve solo las reservas del usuario logueado.
     *
     * @param authentication Usuario autenticado (inyectado automáticamente por Spring Security)
     * @return Lista de ReservaDTO del usuario
     */
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaDTO>> listarMisReservas(Authentication authentication) {
        List<ReservaDTO> reservas = reservaService.listarMisReservas(authentication);
        return ResponseEntity.ok(reservas);
    }

    /**
     * CREAR NUEVA RESERVA
     *
     * POST /reservas
     *
     * PERMISOS: ADMIN y PROFESOR
     * Cualquier usuario autenticado puede crear reservas.
     *
     * VALIDACIONES AUTOMÁTICAS (en el servicio):
     * 1. La fecha no puede ser pasada
     * 2. No puede haber solapamiento (misma aula, fecha y horario)
     * 3. El número de asistentes no puede superar la capacidad del aula
     *
     * Body JSON ejemplo:
     * {
     *   "fecha": "2025-05-20",
     *   "motivo": "Reunión de departamento",
     *   "numeroAsistentes": 15,
     *   "aulaId": 1,
     *   "tramoHorarioId": 3
     * }
     *
     * @param request Datos de la reserva (validado con @Valid)
     * @param authentication Usuario autenticado (se asigna automáticamente como creador)
     * @return ReservaDTO de la reserva creada
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    public ResponseEntity<ReservaDTO> crear(
            @Valid @RequestBody ReservaRequest request,
            Authentication authentication) {

        // El servicio se encarga de todas las validaciones
        ReservaDTO reserva = reservaService.crear(request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    /**
     * ELIMINAR RESERVA
     *
     * DELETE /reservas/{id}
     *
     * PERMISOS:
     * - ADMIN: Puede eliminar cualquier reserva
     * - PROFESOR: Solo puede eliminar sus propias reservas
     *
     * La validación de permisos se hace en el servicio.
     *
     * @param id ID de la reserva a eliminar
     * @param authentication Usuario autenticado
     * @return 204 No Content si se eliminó correctamente
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication) {

        // El servicio verifica que el usuario tenga permiso para eliminar
        reservaService.eliminar(id, authentication);

        return ResponseEntity.noContent().build();
    }

    /**
     * LISTAR RESERVAS DE UN USUARIO ESPECÍFICO
     *
     * GET /reservas/usuario/{usuarioId}
     *
     * PERMISOS: Solo ADMIN
     * Útil para que un admin vea las reservas de un profesor específico.
     *
     * @param usuarioId ID del usuario
     * @return Lista de ReservaDTO del usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN
    public ResponseEntity<List<ReservaDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<ReservaDTO> reservas = reservaService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(reservas);
    }
}