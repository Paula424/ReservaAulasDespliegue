package org.example.reservaaulasdespliegue.Repositories;

import org.example.reservaaulasdespliegue.Entities.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================
 * REPOSITORIO DE AULA
 * ============================================
 *
 * Interface que gestiona el acceso a datos de la entidad Aula.
 * Spring Data JPA genera automáticamente la implementación de todos los métodos.
 *
 * Hereda de JpaRepository que proporciona métodos básicos como:
 * - save(), findById(), findAll(), deleteById(), etc.
 */
@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {

    /**
     * BUSCAR AULAS POR CAPACIDAD MÍNIMA
     *
     * Encuentra todas las aulas que tengan una capacidad igual o superior a la especificada.
     * Usado en: GET /aulas?capacidad=20
     *
     * @param capacidad Capacidad mínima buscada
     * @return Lista de aulas que cumplen el criterio
     *
     * Ejemplo: findByCapacidadGreaterThanEqual(25)
     * Devuelve: [Aula 101 (cap:30), Aula 201 (cap:25), Salón (cap:50)]
     */
    List<Aula> findByCapacidadGreaterThanEqual(Integer capacidad);

    /**
     * BUSCAR AULAS CON ORDENADORES
     *
     * Encuentra todas las aulas que tienen ordenadores (esAulaDeOrdenadores = true).
     * Usado en: GET /aulas?ordenadores=true
     *
     * @return Lista de aulas que tienen ordenadores
     *
     * Ejemplo de SQL generado:
     * SELECT * FROM aulas WHERE es_aula_de_ordenadores = true
     */
    List<Aula> findByEsAulaDeOrdenadoresTrue();

    /**
     * BUSCAR AULA POR NOMBRE (OPCIONAL)
     *
     * Útil para verificar si ya existe un aula con ese nombre antes de crearla.
     *
     * @param nombre Nombre del aula
     * @return Optional con el aula si existe, vacío si no
     */
    Optional<Aula> findByNombre(String nombre);
}
