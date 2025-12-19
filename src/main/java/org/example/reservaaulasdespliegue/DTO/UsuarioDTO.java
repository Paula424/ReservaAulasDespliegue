package org.example.reservaaulasdespliegue.DTO;
/**
 * DTO de respuesta con datos de un USUARIO
 *
 * Se devuelve cuando se consulta información de usuarios.
 * NO incluye la contraseña por seguridad.
 *
 * Ejemplo JSON:
 * {
 *   "id": 1,
 *   "nombre": "Juan Pérez",
 *   "email": "juan.perez@colegio.es",
 *   "role": "ROLE_PROFESOR",
 *   "enabled": true
 * }
 */
public record UsuarioDTO(
        Long id,
        String nombre,
        String email,
        String role,
        boolean enabled
) {}