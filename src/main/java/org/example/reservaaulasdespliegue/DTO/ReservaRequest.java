package org.example.reservaaulasdespliegue.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO para crear una nueva RESERVA
 *
 * Se usa en POST /reservas
 * El usuario debe especificar:
 * - Fecha de la reserva
 * - Motivo
 * - Número de asistentes
 * - ID del aula que reserva
 * - ID del tramo horario
 *
 * Ejemplo JSON:
 * {
 *   "fecha": "2025-05-20",
 *   "motivo": "Reunión de departamento",
 *   "numeroAsistentes": 15,
 *   "aulaId": 1,
 *   "tramoHorarioId": 3
 * }
 */
public record ReservaRequest(
        @NotNull(message = "La fecha es obligatoria")
        @Future(message = "La fecha debe ser futura") // No permitir reservas en el pasado
        LocalDate fecha,

        @NotBlank(message = "El motivo es obligatorio")
        String motivo,

        @NotNull(message = "El número de asistentes es obligatorio")
        @Min(value = 1, message = "Debe haber al menos 1 asistente")
        Integer numeroAsistentes,

        @NotNull(message = "El ID del aula es obligatorio")
        Long aulaId, // ID del aula que se quiere reservar

        @NotNull(message = "El ID del tramo horario es obligatorio")
        Long tramoHorarioId // ID del horario (ej: Lunes 3ª hora)
) {}

