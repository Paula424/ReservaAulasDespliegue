package org.example.reservaaulasdespliegue.DTO;

import java.time.LocalDate;

/**
 * DTO simplificado de RESERVA (sin objetos anidados)
 *
 * Se usa cuando queremos devolver reservas de forma más ligera,
 * por ejemplo en listas grandes o cuando el cliente solo necesita IDs.
 *
 * Ejemplo JSON:
 * {
 *   "id": 1,
 *   "fecha": "2025-05-20",
 *   "motivo": "Reunión de departamento",
 *   "numeroAsistentes": 15,
 *   "fechaCreacion": "2025-04-10",
 *   "aulaId": 1,
 *   "aulaNombre": "Aula 101",
 *   "tramoHorarioId": 3,
 *   "usuarioId": 2,
 *   "usuarioNombre": "Juan Pérez"
 * }
 */
public record ReservaSimpleDTO(
        Long id,
        LocalDate fecha,
        String motivo,
        Integer numeroAsistentes,
        LocalDate fechaCreacion,
        Long aulaId,           // Solo el ID del aula
        String aulaNombre,     // Solo el nombre del aula
        Long tramoHorarioId,   // Solo el ID del tramo
        Long usuarioId,        // Solo el ID del usuario
        String usuarioNombre   // Solo el nombre del usuario
) {}