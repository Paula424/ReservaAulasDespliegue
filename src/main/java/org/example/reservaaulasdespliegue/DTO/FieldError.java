package org.example.reservaaulasdespliegue.DTO;

// ========== FIELD ERROR ==========
/**
 * DTO para representar un ERROR EN UN CAMPO específico
 *
 * Se usa dentro de ValidationErrorResponse para detallar
 * qué campo falló y por qué.
 */
public record FieldError(
        String campo,   // Nombre del campo que falló (ej: "email", "password")
        String mensaje  // Mensaje de error (ej: "El email es obligatorio")
) {}

