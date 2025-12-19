package org.example.reservaaulasdespliegue.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitud de LOGIN
 *
 * Se envía cuando un usuario quiere iniciar sesión.
 * Solo necesita email y contraseña.
 *
 * Ejemplo JSON:
 * {
 *   "email": "profesor@colegio.es",
 *   "password": "123456"
 * }
 */
public record LoginRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
