package org.example.reservaaulasdespliegue.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para solicitud de REGISTRO
 *
 * Se envía cuando se quiere crear un nuevo usuario.
 * Incluye nombre, email, contraseña y rol.
 *
 * Ejemplo JSON:
 * {
 *   "nombre": "Juan Pérez",
 *   "email": "juan.perez@colegio.es",
 *   "password": "123456",
 *   "role": "ROLE_PROFESOR"
 * }
 */
public record RegisterRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        @NotBlank(message = "El rol es obligatorio")
        String role // "ROLE_ADMIN" o "ROLE_PROFESOR"
) {}