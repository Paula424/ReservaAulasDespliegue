package org.example.reservaaulasdespliegue.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar datos de un usuario (sin cambiar password)
 *
 * Permite modificar nombre, email y rol.
 * La contraseña se cambia por otro endpoint.
 *
 * Ejemplo JSON:
 * {
 *   "nombre": "Juan Pérez García",
 *   "email": "juan.garcia@colegio.es",
 *   "role": "ROLE_ADMIN"
 * }
 */
public record ActualizarUsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        String email,

        @NotBlank(message = "El rol es obligatorio")
        String role
) {}