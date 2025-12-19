package org.example.reservaaulasdespliegue.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.reservaaulasdespliegue.DTO.ActualizarUsuarioRequest;
import org.example.reservaaulasdespliegue.DTO.CambiarPasswordRequest;
import org.example.reservaaulasdespliegue.DTO.SuccessResponse;
import org.example.reservaaulasdespliegue.DTO.UsuarioDTO;
import org.example.reservaaulasdespliegue.Services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================
 * CONTROLADOR DE USUARIOS
 * ============================================
 *
 * Maneja la gestión de usuarios del sistema.
 *
 * RUTAS BASE: /usuario
 *
 * PERMISOS:
 * - GET /usuario (listar todos): Solo ADMIN
 * - GET /usuario/{id}: ADMIN y PROFESOR (el profesor solo puede ver su perfil)
 * - PUT /usuario/{id}: ADMIN (cualquiera) o PROFESOR (solo su perfil)
 * - DELETE /usuario/{id}: Solo ADMIN
 * - PATCH /usuario/cambiar-pass: ADMIN y PROFESOR (su propia contraseña)
 */
@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    // Servicio con la lógica de negocio
    private final UsuarioService usuarioService;

    /**
     * LISTAR TODOS LOS USUARIOS
     *
     * GET /usuario
     *
     * PERMISOS: Solo ADMIN
     *
     * Devuelve todos los usuarios registrados en el sistema.
     *
     * @return Lista de UsuarioDTO
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede ver todos los usuarios
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * OBTENER UN USUARIO ESPECÍFICO
     *
     * GET /usuario/{id}
     *
     * PERMISOS: ADMIN y PROFESOR
     * (Un profesor solo debería poder ver su propio perfil,
     * pero esta validación se puede añadir en el servicio si se desea)
     *
     * @param id ID del usuario
     * @return UsuarioDTO con los datos del usuario
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id, Authentication authentication) {
        UsuarioDTO usuario = usuarioService.obtenerPorId(id, authentication);
        return ResponseEntity.ok(usuario);
    }

    /**
     * ACTUALIZAR DATOS DE USUARIO
     *
     * PUT /usuario/{id}
     *
     * PERMISOS:
     * - ADMIN: Puede actualizar cualquier usuario
     * - PROFESOR: Solo puede actualizar sus propios datos (y no su rol)
     *
     * La validación de permisos se hace en el servicio.
     *
     * Body JSON ejemplo:
     * {
     *   "nombre": "Juan Pérez García",
     *   "email": "juan.garcia@colegio.es",
     *   "role": "ROLE_PROFESOR"
     * }
     *
     * @param id ID del usuario a actualizar
     * @param request Nuevos datos (validado con @Valid)
     * @param authentication Usuario autenticado
     * @return UsuarioDTO con los datos actualizados
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequest request,
            Authentication authentication) {

        UsuarioDTO usuario = usuarioService.actualizar(id, request, authentication);
        return ResponseEntity.ok(usuario);
    }

    /**
     * ELIMINAR USUARIO
     *
     * DELETE /usuario/{id}
     *
     * PERMISOS: Solo ADMIN
     *
     * Elimina un usuario del sistema.
     * Un admin no puede eliminarse a sí mismo.
     *
     * @param id ID del usuario a eliminar
     * @param authentication Usuario autenticado (debe ser ADMIN)
     * @return 204 No Content si se eliminó correctamente
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede eliminar usuarios
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication) {

        usuarioService.eliminar(id, authentication);
        return ResponseEntity.noContent().build();
    }

    /**
     * CAMBIAR CONTRASEÑA
     *
     * PATCH /usuario/cambiar-pass
     *
     * PERMISOS: ADMIN y PROFESOR
     * Cualquier usuario puede cambiar su propia contraseña.
     *
     * Body JSON ejemplo:
     * {
     *   "passwordActual": "123456",
     *   "passwordNueva": "nuevaPass123"
     * }
     *
     * @param request Contraseña actual y nueva (validado con @Valid)
     * @param authentication Usuario autenticado
     * @return SuccessResponse confirmando el cambio
     */
    @PatchMapping("/cambiar-pass")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    public ResponseEntity<SuccessResponse> cambiarPassword(
            @Valid @RequestBody CambiarPasswordRequest request,
            Authentication authentication) {

        usuarioService.cambiarPassword(request, authentication);

        // Respuesta de éxito
        SuccessResponse response = new SuccessResponse(
                LocalDateTime.now(),
                200,
                "Contraseña actualizada correctamente"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * HABILITAR/DESHABILITAR USUARIO
     *
     * PATCH /usuario/{id}/estado?enabled=true
     *
     * PERMISOS: Solo ADMIN
     *
     * Permite activar o desactivar una cuenta de usuario.
     * Los usuarios deshabilitados no pueden hacer login.
     * Un admin no puede deshabilitarse a sí mismo.
     *
     * @param id ID del usuario
     * @param enabled true para habilitar, false para deshabilitar
     * @param authentication Usuario autenticado (debe ser ADMIN)
     * @return UsuarioDTO con el estado actualizado
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN
    public ResponseEntity<UsuarioDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean enabled,
            Authentication authentication) {

        UsuarioDTO usuario = usuarioService.cambiarEstado(id, enabled, authentication);
        return ResponseEntity.ok(usuario);
    }

    /**
     * LISTAR USUARIOS POR ROL
     *
     * GET /usuario/rol/{role}
     *
     * PERMISOS: Solo ADMIN
     *
     * Filtra usuarios por rol.
     * Útil para listar solo profesores o solo admins.
     *
     * @param role Rol a filtrar ("ROLE_ADMIN" o "ROLE_PROFESOR")
     * @return Lista de UsuarioDTO con ese rol
     */
    @GetMapping("/rol/{role}")
    @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN
    public ResponseEntity<List<UsuarioDTO>> listarPorRol(@PathVariable String role) {
        List<UsuarioDTO> usuarios = usuarioService.listarPorRol(role);
        return ResponseEntity.ok(usuarios);
    }
}
