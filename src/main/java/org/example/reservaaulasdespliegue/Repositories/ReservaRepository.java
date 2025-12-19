package org.example.reservaaulasdespliegue.Repositories;

import org.example.reservaaulasdespliegue.Entities.Aula;
import org.example.reservaaulasdespliegue.Entities.Horario;
import org.example.reservaaulasdespliegue.Entities.Reserva;
import org.example.reservaaulasdespliegue.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * ============================================
 * REPOSITORIO DE RESERVA
 * ============================================
 *
 * Interface que gestiona el acceso a datos de la entidad Reserva.
 * Incluye métodos personalizados para validaciones de negocio.
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /**
     * BUSCAR RESERVAS DE UN AULA EN UNA FECHA
     *
     * Encuentra todas las reservas de un aula específica en una fecha concreta.
     * Útil para ver qué horarios están ocupados en un día.
     *
     * @param aula Aula de la que queremos ver reservas
     * @param fecha Fecha a consultar
     * @return Lista de reservas en esa aula ese día
     */
    List<Reserva> findByAulaAndFecha(Aula aula, LocalDate fecha);

    /**
     * VALIDAR SOLAPAMIENTO DE RESERVAS (MÉTODO CRÍTICO)
     *
     * Busca si ya existe una reserva para:
     * - La misma aula
     * - La misma fecha
     * - El mismo tramo horario
     *
     * Se usa ANTES de crear una reserva para evitar conflictos.
     * Si devuelve alguna reserva, significa que hay solapamiento.
     *
     * @param aula Aula que se quiere reservar
     * @param fecha Fecha de la reserva
     * @param horario Tramo horario (ej: Lunes 3ª hora)
     * @return Lista de reservas que coinciden (debería estar vacía o tener máximo 1 elemento)
     *
     * Ejemplo de uso en servicio:
     * List<Reserva> conflictos = repository.findByAulaAndFechaAndTramoHorario(aula, fecha, tramo);
     * if (!conflictos.isEmpty()) {
     *     throw new Exception("Ya hay una reserva en ese horario");
     * }
     */
    List<Reserva> findByAulaAndFechaAndTramoHorario(
            Aula aula,
            LocalDate fecha,
            Horario horario
    );

    /**
     * BUSCAR RESERVAS DE UN USUARIO
     *
     * Encuentra todas las reservas creadas por un usuario específico.
     * Útil para que un profesor vea sus propias reservas.
     *
     * @param usuario Usuario del que queremos ver reservas
     * @return Lista de reservas del usuario
     */
    List<Reserva> findByUsuario(Usuario usuario);

    /**
     * BUSCAR RESERVAS DE UN USUARIO POR ID
     *
     * Similar al anterior pero usando directamente el ID del usuario.
     * Más eficiente si ya tenemos el ID.
     *
     * @param usuarioId ID del usuario
     * @return Lista de reservas del usuario
     */
    List<Reserva> findByUsuarioId(Long usuarioId);

    /**
     * BUSCAR RESERVAS DE UN AULA (todas las fechas)
     *
     * Encuentra todas las reservas de un aula específica.
     * Usado en: GET /aulas/{id}/reservas
     *
     * @param aula Aula de la que queremos ver todas las reservas
     * @return Lista de reservas del aula
     */
    List<Reserva> findByAula(Aula aula);

    /**
     * BUSCAR RESERVAS POR AULA ID
     *
     * Similar al anterior pero usando el ID directamente.
     *
     * @param aulaId ID del aula
     * @return Lista de reservas del aula
     */
    List<Reserva> findByAulaId(Long aulaId);

    /**
     * VALIDAR SOLAPAMIENTO (alternativa con Query personalizada)
     *
     * Esta es una forma alternativa de validar solapamientos usando JPQL.
     * Es más explícita y permite excluir una reserva específica al actualizar.
     *
     * @param aulaId ID del aula
     * @param fecha Fecha de la reserva
     * @param tramoHorarioId ID del tramo horario
     * @param reservaId ID de la reserva a excluir (útil al actualizar)
     * @return true si existe solapamiento, false si no
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Reserva r " +
            "WHERE r.aula.id = :aulaId " +
            "AND r.fecha = :fecha " +
            "AND r.tramoHorario.id = :tramoHorarioId " +
            "AND r.id != :reservaId")
    boolean existeSolapamiento(
            @Param("aulaId") Long aulaId,
            @Param("fecha") LocalDate fecha,
            @Param("tramoHorarioId") Long tramoHorarioId,
            @Param("reservaId") Long reservaId
    );
}
