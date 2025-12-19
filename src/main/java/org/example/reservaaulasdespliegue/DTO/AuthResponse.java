package org.example.reservaaulasdespliegue.DTO;

/**
 * DTO de respuesta después de LOGIN o REGISTER exitoso
 *
 * Devuelve el token JWT y los datos del usuario.
 * El cliente guarda este token y lo envía en cada petición.
 *
 * Ejemplo JSON:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "usuario": { ... }
 * }
 */
public record AuthResponse(
        String token,        // Token JWT para autenticación
        UsuarioDTO usuario   // Datos del usuario logueado
) {}
