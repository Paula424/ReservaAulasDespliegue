package org.example.reservaaulasdespliegue.DTO;
/**
 * ============================================
 * DTOs DE RESPUESTAS DE ERROR
 * ============================================
 * <p>
 * Estos DTOs se usan en el GlobalExceptionHandler para devolver
 * respuestas consistentes cuando ocurre un error en la API.
 */

// ========== ERROR RESPONSE ==========

import java.time.LocalDateTime;

/**
 * DTO estándar para respuestas de ERROR
 *
 * Se usa cuando ocurre cualquier error en la API (validación, negocio, etc.)
 * Proporciona información clara al cliente sobre qué salió mal.
 *
 * Ejemplo JSON (error simple):
 * {
 *   "timestamp": "2025-05-20T10:30:00",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "mensaje": "El número de asistentes supera la capacidad del aula",
 *   "path": "/api/reservas"
 * }
 */
public record ErrorResponse(
        LocalDateTime timestamp,  // Cuándo ocurrió el error
        int status,               // Código HTTP (400, 404, 500...)
        String error,             // Tipo de error (Bad Request, Not Found...)
        String mensaje,           // Mensaje descriptivo del error
        String path               // Endpoint donde ocurrió el error
) {
}