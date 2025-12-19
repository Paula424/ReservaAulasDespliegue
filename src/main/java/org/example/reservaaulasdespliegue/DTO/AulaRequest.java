package org.example.reservaaulasdespliegue.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear o actualizar un AULA
 *
 * Se usa tanto en POST /aulas como en PUT /aulas/{id}
 * Incluye todas las características del aula.
 *
 * Ejemplo JSON:
 * {
 *   "nombre": "Aula 101",
 *   "capacidad": 30,
 *   "esAulaDeOrdenadores": true,
 *   "numeroOrdenadores": 15
 * }
 */
public record AulaRequest(
        @NotBlank(message = "El nombre del aula es obligatorio")
        String nombre,

        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser al menos 1")
        Integer capacidad,

        @NotNull(message = "Debe indicar si es aula de ordenadores")
        Boolean esAulaDeOrdenadores,

        // Este campo es opcional, solo necesario si esAulaDeOrdenadores = true
        @Min(value = 0, message = "El número de ordenadores no puede ser negativo")
        Integer numeroOrdenadores
) {}
