package org.example.reservaaulasdespliegue.Repositories;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.example.reservaaulasdespliegue.Entities.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================
 * REPOSITORIO DE  HORARIO
 * ============================================
 *
 * Interface que gestiona el acceso a datos de la entidad Horario.
 * Por ahora solo usa los métodos básicos de JpaRepository.
 */
@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    /**
     * BUSCAR TRAMOS POR DÍA DE LA SEMANA
     *
     * Encuentra todos los tramos horarios de un día específico.
     * Útil para mostrar el horario completo de un día.
     *
     * @param diaSemana Día de la semana (LUNES, MARTES, etc.)
     * @return Lista de tramos horarios de ese día
     */
    List<Horario> findByDiaSemana(Horario.DiaSemana diaSemana);

    /**
     * BUSCAR TRAMOS POR TIPO
     *
     * Encuentra todos los tramos de un tipo específico.
     * Ejemplo: Encontrar todos los RECREOS, todas las horas LECTIVAS, etc.
     *
     * @param tipo Tipo de tramo (LECTIVA, RECREO, MEDIODIA)
     * @return Lista de tramos de ese tipo
     */
    List<Horario> findByTipo(Horario.TipoTramo tipo);

    /**
     * BUSCAR TRAMO ESPECÍFICO POR DÍA Y SESIÓN
     *
     * Encuentra un tramo horario específico (ej: Lunes 3ª hora).
     * Útil para verificar que no se dupliquen tramos.
     *
     * @param diaSemana Día de la semana
     * @param sesionDia Número de sesión (1, 2, 3...)
     * @return Optional con el tramo si existe, vacío si no
     */
    Optional<Horario> findByDiaSemanaAndSesionDia(
            Horario.DiaSemana diaSemana,
            Integer sesionDia
    );

}
