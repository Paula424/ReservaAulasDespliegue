package org.example.reservaaulasdespliegue.DTO;
/**
 * ============================================
 * DTOs DE RESERVA
 * ============================================
 */
// ========== RESERVA DTO (Response) ==========

import java.time.LocalDate;

/**
 * DTO de respuesta con datos de una RESERVA
 *
 * Se devuelve en las consultas GET.
 * Incluye toda la información de la reserva más los datos del aula,
 * tramo horario y usuario asociados.
 *
 * Ejemplo JSON:
 * {
 *   "id": 1,
 *   "fecha": "2025-05-20",
 *   "motivo": "Reunión de departamento",
 *   "numeroAsistentes": 15,
 *   "fechaCreacion": "2025-04-10",
 *   "aula": { "id": 1, "nombre": "Aula 101", ... },
 *   "tramoHorario": { "id": 3, "diaSemana": "LUNES", ... },
 *   "usuario": { "id": 2, "nombre": "Juan Pérez", ... }
 * }
 */
public record ReservaDTO(
        Long id,
        LocalDate fecha,
        String motivo,
        Integer numeroAsistentes,
        LocalDate fechaCreacion,
        AulaDTO aula,                      // Datos del aula reservada
        HorarioDTO tramoHorario,      // Datos del horario
        UsuarioDTO usuario                 // Datos del usuario que la creó
) {}