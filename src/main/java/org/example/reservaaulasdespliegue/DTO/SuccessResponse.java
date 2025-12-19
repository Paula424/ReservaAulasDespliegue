package org.example.reservaaulasdespliegue.DTO;


import java.time.LocalDateTime;

/**
 * DTO estándar para respuestas EXITOSAS
 *
 * Se usa para operaciones que no devuelven datos pero confirman el éxito.
 * Ejemplos: DELETE exitoso, cambio de contraseña exitoso, etc.
 *
 * Ejemplo JSON:
 * {
 *   "timestamp": "2025-05-20T10:30:00",
 *   "status": 200,
 *   "mensaje": "Reserva eliminada correctamente"
 * }
 */
public record SuccessResponse(
        LocalDateTime timestamp,
        int status,
        String mensaje
) {}
