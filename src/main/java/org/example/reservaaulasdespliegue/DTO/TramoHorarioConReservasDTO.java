package org.example.reservaaulasdespliegue.DTO;

import org.example.reservaaulasdespliegue.Entities.Horario;

import java.time.LocalTime;

/**
 * DTO de respuesta con un TRAMO HORARIO y sus RESERVAS
 *
 * Se usa cuando queremos ver qué reservas hay en un tramo horario específico.
 * Útil para detectar solapamientos o ver disponibilidad.
 *
 * Ejemplo JSON:
 * {
 *   "id": 3,
 *   "diaSemana": "LUNES",
 *   "sesionDia": 3,
 *   "horaInicio": "10:30:00",
 *   "horaFin": "11:25:00",
 *   "tipo": "LECTIVA",
 *   "reservas": [
 *     { "id": 1, "fecha": "2025-05-20", ... },
 *     { "id": 5, "fecha": "2025-05-27", ... }
 *   ]
 * }
 */
public record TramoHorarioConReservasDTO(
        Long id,
        Horario.DiaSemana diaSemana,
        Integer sesionDia,
        LocalTime horaInicio,
        LocalTime horaFin,
        Horario.TipoTramo tipo,
        java.util.List<ReservaSimpleDTO> reservas // Lista de reservas en este tramo
) {}
