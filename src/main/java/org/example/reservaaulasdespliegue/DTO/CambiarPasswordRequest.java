package org.example.reservaaulasdespliegue.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para cambiar la contraseña de un usuario
 *
 * El usuario envía su contraseña actual y la nueva.
 * Se valida que la contraseña actual sea correcta antes de cambiarla.
 *
 * Ejemplo JSON:
 * {
 *   "passwordActual": "123456",
 *   "passwordNueva": "nuevaPass123"
 * }
 */
public record CambiarPasswordRequest(
        @NotBlank(message = "La contraseña actual es obligatoria")
        String passwordActual,

        @NotBlank(message = "La contraseña nueva es obligatoria")
        @Size(min = 6, message = "La contraseña nueva debe tener al menos 6 caracteres")
        String passwordNueva
) {}
