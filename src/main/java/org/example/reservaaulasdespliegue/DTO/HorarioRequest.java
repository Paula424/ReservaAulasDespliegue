package org.example.reservaaulasdespliegue.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.example.reservaaulasdespliegue.Entities.Horario;

import java.time.LocalTime;
/**
 * DTO para crear o actualizar un TRAMO HORARIO
 *
 * Se usa en POST /tramo-horario y PUT /tramo-horario/{id}
 * Define una franja horaria específica del horario escolar.
 *
 * Ejemplo JSON:
 * {
 *   "diaSemana": "LUNES",
 *   "sesionDia": 3,
 *   "horaInicio": "10:30:00",
 *   "horaFin": "11:25:00",
 *   "tipo": "LECTIVA"
 * }
 */
public record HorarioRequest(
        @NotNull(message = "El día de la semana es obligatorio")
        Horario.DiaSemana diaSemana, // LUNES, MARTES, MIERCOLES, JUEVES, VIERNES

        @NotNull(message = "La sesión del día es obligatoria")
        @Min(value = 1, message = "La sesión debe ser al menos 1")
        Integer sesionDia, // 1ª hora, 2ª hora, 3ª hora...

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio, // Ej: 10:30

        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin, // Ej: 11:25

        @NotNull(message = "El tipo de tramo es obligatorio")
        Horario.TipoTramo tipo // LECTIVA, RECREO, MEDIODIA
) {
    /**
     * VALIDACIÓN ADICIONAL EN EL CONSTRUCTOR
     *
     * Verifica que la hora de inicio sea anterior a la hora de fin.
     * Si no, lanza una excepción.
     */
    public HorarioRequest {
        // Validar que horaInicio sea antes que horaFin
        if (horaInicio != null && horaFin != null && horaInicio.isAfter(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }
    }
}
