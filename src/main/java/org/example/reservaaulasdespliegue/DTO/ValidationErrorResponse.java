package org.example.reservaaulasdespliegue.DTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuestas de ERROR DE VALIDACIÓN
 *
 * Se usa cuando fallan las validaciones de @Valid en los DTOs.
 * Incluye una lista de todos los campos que fallaron la validación.
 *
 * Ejemplo JSON:
 * {
 *   "timestamp": "2025-05-20T10:30:00",
 *   "status": 400,
 *   "error": "Validation Failed",
 *   "mensaje": "Error de validación en los datos enviados",
 *   "path": "/api/reservas",
 *   "errores": [
 *     {
 *       "campo": "fecha",
 *       "mensaje": "La fecha debe ser futura"
 *     },
 *     {
 *       "campo": "numeroAsistentes",
 *       "mensaje": "El número de asistentes es obligatorio"
 *     }
 *   ]
 * }
 */
public record ValidationErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String path,
        List<FieldError> errores  // Lista de errores de validación
) {}
