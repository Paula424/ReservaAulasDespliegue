package org.example.reservaaulasdespliegue.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.reservaaulasdespliegue.DTO.AulaConReservasDTO;
import org.example.reservaaulasdespliegue.DTO.AulaDTO;
import org.example.reservaaulasdespliegue.DTO.AulaRequest;
import org.example.reservaaulasdespliegue.Services.AulaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================
 * CONTROLADOR DE AULAS
 * ============================================
 *
 * Maneja todos los endpoints relacionados con aulas.
 *
 * RUTAS BASE: /aulas
 *
 * PERMISOS:
 * - GET (listar/consultar): ADMIN y PROFESOR
 * - POST (crear): Solo ADMIN
 * - PUT (actualizar): Solo ADMIN
 * - DELETE (eliminar): Solo ADMIN
 */
@RestController
@RequestMapping("/aulas")
@RequiredArgsConstructor // Inyección de dependencias por constructor
public class AulaController {

    // Servicio que contiene la lógica de negocio
    private final AulaService aulaService;

    /**
     * LISTAR TODAS LAS AULAS (con filtros opcionales)
     *
     * GET /aulas - Lista todas las aulas
     * GET /aulas?capacidad=20 - Filtra aulas con capacidad >= 20
     * GET /aulas?ordenadores=true - Filtra aulas con ordenadores
     *
     * PERMISOS: ADMIN y PROFESOR
     *
     * @param capacidad (Opcional) Capacidad mínima
     * @param ordenadores (Opcional) Filtrar por aulas con ordenadores
     * @return Lista de AulaDTO
     */
    @GetMapping
    public ResponseEntity<List<AulaDTO>> listar(
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false) Boolean ordenadores) {

        // Si hay filtro de capacidad
        if (capacidad != null) {
            List<AulaDTO> aulas = aulaService.buscarPorCapacidad(capacidad);
            return ResponseEntity.ok(aulas);
        }

        // Si hay filtro de ordenadores
        if (ordenadores != null && ordenadores) {
            List<AulaDTO> aulas = aulaService.buscarConOrdenadores();
            return ResponseEntity.ok(aulas);
        }

        // Sin filtros: devolver todas
        List<AulaDTO> aulas = aulaService.listarTodas();
        return ResponseEntity.ok(aulas);
    }

    /**
     * OBTENER DETALLES DE UN AULA
     *
     * GET /aulas/{id}
     *
     * PERMISOS: ADMIN y PROFESOR
     *
     * @param id ID del aula
     * @return AulaDTO con los datos del aula
     */
    @GetMapping("/{id}")
    public ResponseEntity<AulaDTO> obtener(@PathVariable Long id) {
        AulaDTO aula = aulaService.obtenerPorId(id);
        return ResponseEntity.ok(aula);
    }

    /**
     * OBTENER RESERVAS DE UN AULA
     *
     * GET /aulas/{id}/reservas
     *
     * Devuelve el aula con todas sus reservas.
     * PERMISOS: ADMIN y PROFESOR
     *
     * @param id ID del aula
     * @return AulaConReservasDTO (aula + lista de reservas)
     */
    @GetMapping("/{id}/reservas")
    public ResponseEntity<AulaConReservasDTO> obtenerReservasDeAula(@PathVariable Long id) {
        AulaConReservasDTO aula = aulaService.obtenerConReservas(id);
        return ResponseEntity.ok(aula);
    }

    /**
     * CREAR NUEVA AULA
     *
     * POST /aulas
     *
     * PERMISOS: Solo ADMIN
     *
     * Body JSON ejemplo:
     * {
     *   "nombre": "Aula 101",
     *   "capacidad": 30,
     *   "esAulaDeOrdenadores": true,
     *   "numeroOrdenadores": 15
     * }
     *
     * @param request Datos del aula a crear (validado con @Valid)
     * @return AulaDTO del aula creada
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede crear aulas
    public ResponseEntity<AulaDTO> crear(@Valid @RequestBody AulaRequest request) {
        AulaDTO aula = aulaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(aula);
    }

    /**
     * ACTUALIZAR AULA
     *
     * PUT /aulas/{id}
     *
     * PERMISOS: Solo ADMIN
     *
     * Body JSON ejemplo:
     * {
     *   "nombre": "Aula 101 Renovada",
     *   "capacidad": 35,
     *   "esAulaDeOrdenadores": true,
     *   "numeroOrdenadores": 20
     * }
     *
     * @param id ID del aula a actualizar
     * @param request Nuevos datos del aula (validado con @Valid)
     * @return AulaDTO con los datos actualizados
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede actualizar aulas
    public ResponseEntity<AulaDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AulaRequest request) {

        AulaDTO aula = aulaService.actualizar(id, request);
        return ResponseEntity.ok(aula);
    }

    /**
     * ELIMINAR AULA
     *
     * DELETE /aulas/{id}
     *
     * PERMISOS: Solo ADMIN
     *
     * IMPORTANTE: Si el aula tiene reservas, se eliminarán también (cascade)
     *
     * @param id ID del aula a eliminar
     * @return 204 No Content si se eliminó correctamente
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede eliminar aulas
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        aulaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}