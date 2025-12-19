package org.example.reservaaulasdespliegue.Repositories;

import org.example.reservaaulasdespliegue.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================
 * REPOSITORIO DE USUARIO
 * ============================================
 *
 * Interface que gestiona el acceso a datos de la entidad Usuario.
 * Incluye métodos para autenticación y búsqueda por email.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * BUSCAR USUARIO POR EMAIL
     *
     * Método CRÍTICO para el login y autenticación.
     * Spring Security usa este método para cargar los datos del usuario.
     *
     * @param email Email del usuario
     * @return Optional con el usuario si existe, vacío si no
     *
     * Ejemplo de uso:
     * Optional<Usuario> usuario = repository.findByEmail("juan@colegio.es");
     * if (usuario.isPresent()) {
     *     // Usuario encontrado, validar contraseña
     * } else {
     *     // Usuario no existe, error de login
     * }
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * VERIFICAR SI EXISTE UN EMAIL
     *
     * Comprueba si ya existe un usuario con ese email.
     * Útil en el registro para evitar emails duplicados.
     *
     * @param email Email a verificar
     * @return true si el email ya existe, false si no
     *
     * Ejemplo:
     * if (repository.existsByEmail("nuevo@colegio.es")) {
     *     throw new Exception("El email ya está registrado");
     * }
     */
    boolean existsByEmail(String email);

    /**
     * VERIFICAR SI EXISTE UN NOMBRE
     *
     * Comprueba si ya existe un usuario con ese nombre.
     * Útil en el registro para evitar nombres duplicados.
     *
     * @param nombre Nombre a verificar
     * @return true si el nombre ya existe, false si no
     */
    boolean existsByNombre(String nombre);

    /**
     * BUSCAR USUARIOS POR ROL
     *
     * Encuentra todos los usuarios con un rol específico.
     * Ejemplo: Listar todos los profesores, listar todos los admins.
     *
     * @param role Rol a buscar ("ROLE_ADMIN" o "ROLE_PROFESOR")
     * @return Lista de usuarios con ese rol
     */
    List<Usuario> findByRole(String role);
}
