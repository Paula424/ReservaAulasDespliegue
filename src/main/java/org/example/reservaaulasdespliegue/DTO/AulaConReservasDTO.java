package org.example.reservaaulasdespliegue.DTO;

/**
 * DTO de respuesta con un AULA y sus RESERVAS
 *
 * Se usa específicamente en GET /aulas/{id}/reservas
 * Incluye la lista completa de reservas del aula.
 *
 * Ejemplo JSON:
 * {
 *   "id": 1,
 *   "nombre": "Aula 101",
 *   "capacidad": 30,
 *   "esAulaDeOrdenadores": true,
 *   "numeroOrdenadores": 15,
 *   "reservas": [
 *     { "id": 1, "fecha": "2025-05-20", ... },
 *     { "id": 2, "fecha": "2025-05-21", ... }
 *   ]
 * }
 */
public record AulaConReservaDTO(
        Long id,
        String nombre,
        Integer capacidad,
        Boolean esAulaDeOrdenadores,
        Integer numeroOrdenadores,
        java.util.List<ReservaDTO> reservas // Lista de reservas asociadas
) {}
