package org.example.reservaaulasdespliegue.Services;

import lombok.RequiredArgsConstructor;
import org.example.reservaaulasdespliegue.DTO.ActualizarUsuarioRequest;
import org.example.reservaaulasdespliegue.DTO.CambiarPasswordRequest;
import org.example.reservaaulasdespliegue.DTO.UsuarioDTO;
import org.example.reservaaulasdespliegue.Entities.Usuario;
import org.example.reservaaulasdespliegue.Repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================
 * SERVICIO DE USUARIOS
 * ============================================
 *
 * Gestiona las operaciones CRUD de usuarios:
 * - Listar usuarios
 * - Actualizar datos de usuario
 * - Eliminar usuarios
 * - Cambiar contraseñas
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * LISTAR TODOS LOS USUARIOS
     *
     * Devuelve todos los usuarios del sistema.
     * Solo accesible para ADMIN.
     *
     * @return Lista de UsuarioDTO
     */
    public List<UsuarioDTO> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * OBTENER USUARIO POR ID
     *
     * Busca un usuario específico por su ID.
     *
     * @param id ID del usuario
     * @return UsuarioDTO
     */
    public UsuarioDTO obtenerPorId(Long id, Authentication authentication) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));
        String emailAuth = authentication.getName();
        Usuario usuarioAuth = usuarioRepository.findByEmail(emailAuth)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        if (usuarioAuth.getRole().equals("ROLE_ADMIN") || usuarioAuth.getId().equals(id)) {
            return new UsuarioDTO(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getRole(),
                    usuario.isEnabled()
            );
        } else {
            throw new RuntimeException("No tienes permiso para ver este usuario");
        }
    }

    /**
     * ACTUALIZAR DATOS DE USUARIO
     *
     * Modifica nombre, email o rol de un usuario.
     * Endpoint: PUT /usuario/{id}
     *
     * REGLAS DE AUTORIZACIÓN:
     * - Los ADMIN pueden actualizar cualquier usuario
     * - Los PROFESOR solo pueden actualizar sus propios datos
     * - Los PROFESOR no pueden cambiar su propio rol
     *
     * @param id ID del usuario a actualizar
     * @param request Nuevos datos
     * @param authentication Usuario autenticado
     * @return UsuarioDTO actualizado
     */
    @Transactional
    public UsuarioDTO actualizar(Long id, ActualizarUsuarioRequest request, Authentication authentication) {
        // Buscar el usuario a actualizar
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));

        // Obtener el usuario autenticado
        String emailAuth = authentication.getName();
        Usuario usuarioAuth = usuarioRepository.findByEmail(emailAuth)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        // Verificar permisos
        boolean esAdmin = usuarioAuth.getRole().equals("ROLE_ADMIN");
        boolean esSuCuenta = usuario.getId().equals(usuarioAuth.getId());

        if (!esAdmin && !esSuCuenta) {
            throw new RuntimeException("No tienes permiso para modificar este usuario");
        }

        // Si es profesor modificando su propia cuenta, no puede cambiar el rol
        if (!esAdmin && !usuario.getRole().equals(request.role())) {
            throw new RuntimeException("No puedes cambiar tu propio rol");
        }

        // Validar que el nuevo email no esté en uso (por otro usuario)
        if (!usuario.getEmail().equals(request.email())) {
            if (usuarioRepository.existsByEmail(request.email())) {
                throw new RuntimeException("El email " + request.email() + " ya está en uso");
            }
        }

        // Validar que el nuevo nombre no esté en uso (por otro usuario)
        if (!usuario.getNombre().equals(request.nombre())) {
            if (usuarioRepository.existsByNombre(request.nombre())) {
                throw new RuntimeException("El nombre " + request.nombre() + " ya está en uso");
            }
        }

        // Actualizar los datos
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setRole(request.role());
        usuario.setEnabled(true);


        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return convertirADTO(usuarioActualizado);
    }

    /**
     * ELIMINAR USUARIO
     *
     * Elimina un usuario del sistema.
     * Endpoint: DELETE /usuario/{id}
     * Solo ADMIN puede eliminar usuarios.
     *
     * IMPORTANTE: Si el usuario tiene reservas, se eliminarán también
     * (por cascade en la entidad Usuario)
     *
     * @param id ID del usuario a eliminar
     * @param authentication Usuario autenticado
     */
    @Transactional
    public void eliminar(Long id, Authentication authentication) {
        // Verificar que existe
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario con ID " + id + " no encontrado");
        }

        // Verificar que es ADMIN
        String emailAuth = authentication.getName();
        Usuario usuarioAuth = usuarioRepository.findByEmail(emailAuth)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        if (!usuarioAuth.getRole().equals("ROLE_ADMIN")) {
            throw new RuntimeException("Solo los administradores pueden eliminar usuarios");
        }

        // No permitir que un admin se elimine a sí mismo
        if (usuarioAuth.getId().equals(id)) {
            throw new RuntimeException("No puedes eliminar tu propia cuenta");
        }

        // Eliminar el usuario
        usuarioRepository.deleteById(id);
    }

    /**
     * CAMBIAR CONTRASEÑA
     *
     * Permite a un usuario cambiar su contraseña.
     * Endpoint: PATCH /usuario/cambiar-pass
     *
     * VALIDACIONES:
     * - La contraseña actual debe ser correcta
     * - La nueva contraseña debe cumplir requisitos mínimos
     *
     * @param request Contraseña actual y nueva
     * @param authentication Usuario autenticado
     */
    @Transactional
    public void cambiarPassword(CambiarPasswordRequest request, Authentication authentication) {
        // Obtener el usuario autenticado
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que la contraseña actual es correcta
        if (!passwordEncoder.matches(request.passwordActual(), usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        //comprobar que cumle la longitud lqa contraseña.
        if (request.passwordNueva().length() < 8) {
            throw new RuntimeException("La nueva contraseña debe tener al menos 8 caracteres");
        }
        // Verificar que la nueva contraseña es diferente
        if (request.passwordActual().equals(request.passwordNueva())) {
            throw new RuntimeException("La nueva contraseña debe ser diferente a la actual");
        }

        // Actualizar la contraseña (encriptada)
        usuario.setPassword(passwordEncoder.encode(request.passwordNueva()));

        usuarioRepository.save(usuario);
    }

    /**
     * HABILITAR/DESHABILITAR USUARIO
     *
     * Permite a un ADMIN activar o desactivar una cuenta de usuario.
     * Los usuarios deshabilitados no pueden hacer login.
     *
     * @param id ID del usuario
     * @param enabled true para habilitar, false para deshabilitar
     * @param authentication Usuario autenticado (debe ser ADMIN)
     */
    @Transactional
    public UsuarioDTO cambiarEstado(Long id, boolean enabled, Authentication authentication) {
        // Verificar que es ADMIN
        String emailAuth = authentication.getName();
        Usuario usuarioAuth = usuarioRepository.findByEmail(emailAuth)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        if (!usuarioAuth.getRole().equals("ROLE_ADMIN")) {
            throw new RuntimeException("Solo los administradores pueden cambiar el estado de usuarios");
        }

        // Buscar el usuario a modificar
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));

        // No permitir deshabilitarse a sí mismo
        if (usuarioAuth.getId().equals(id) && !enabled) {
            throw new RuntimeException("No puedes deshabilitarte a ti mismo");
        }

        // Cambiar el estado
        usuario.setEnabled(enabled);

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return convertirADTO(usuarioActualizado);
    }

    /**
     * LISTAR USUARIOS POR ROL
     *
     * Filtra usuarios según su rol.
     * Útil para listar solo profesores o solo admins.
     *
     * @param role Rol a filtrar ("ROLE_ADMIN" o "ROLE_PROFESOR")
     * @return Lista de UsuarioDTO
     */
    public List<UsuarioDTO> listarPorRol(String role) {
        // Validar que el rol sea válido
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_PROFESOR")) {
            throw new RuntimeException("El rol debe ser ROLE_ADMIN o ROLE_PROFESOR");
        }

        List<Usuario> usuarios = usuarioRepository.findByRole(role);

        return usuarios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * CONVERTIR ENTIDAD A DTO (sin contraseña)
     */
    private UsuarioDTO convertirADTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.isEnabled()
        );
    }
}