package org.example.reservaaulasdespliegue.DTO;

/**
 * ============================================
 * DTOs DE AULA
 * ============================================
 */

// ========== AULA DTO (Response) ==========
/**
 * DTO de respuesta con datos de un AULA
 *
 * Se devuelve en las consultas GET.
 * Incluye todos los datos del aula SIN las reservas (para evitar respuestas pesadas).
 *
 * Ejemplo JSON:
 * {
 *   "id": 1,
 *   "nombre": "Aula 101",
 *   "capacidad": 30,
 *   "esAulaDeOrdenadores": true,
 *   "numeroOrdenadores": 15
 * }
 */
public record AulaDTO(
        Long id,
        String nombre,
        Integer capacidad,
        Boolean esAulaDeOrdenadores,
        Integer numeroOrdenadores
) {}
