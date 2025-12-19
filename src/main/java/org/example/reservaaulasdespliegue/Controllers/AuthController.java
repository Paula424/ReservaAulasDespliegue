package org.example.reservaaulasdespliegue.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.reservaaulasdespliegue.DTO.AuthResponse;
import org.example.reservaaulasdespliegue.DTO.LoginRequest;
import org.example.reservaaulasdespliegue.DTO.RegisterRequest;
import org.example.reservaaulasdespliegue.DTO.UsuarioDTO;
import org.example.reservaaulasdespliegue.Services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * ============================================
 * CONTROLADOR DE AUTENTICACIÓN
 * ============================================
 *
 * Maneja el registro, login y perfil de usuarios.
 *
 * RUTAS BASE: /auth
 *
 * ENDPOINTS PÚBLICOS (sin autenticación):
 * - POST /auth/register - Registrar nuevo usuario
 * - POST /auth/login - Iniciar sesión
 *
 * ENDPOINTS PROTEGIDOS (requieren token):
 * - GET /auth/perfil - Ver perfil del usuario autenticado
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    // Servicio de autenticación
    private final AuthService authService;

    /**
     * REGISTRAR NUEVO USUARIO
     *
     * POST /auth/register
     *
     * ACCESO: Público (no requiere autenticación)
     *
     * Crea una cuenta nueva en el sistema.
     * Por defecto, los usuarios se registran con rol ROLE_PROFESOR.
     * Solo un ADMIN puede crear otros ADMIN (desde otro endpoint).
     *
     * Body JSON ejemplo:
     * {
     *   "nombre": "Juan Pérez",
     *   "email": "juan.perez@colegio.es",
     *   "password": "123456",
     *   "role": "ROLE_PROFESOR"
     * }
     *
     * @param request Datos del nuevo usuario (validado con @Valid)
     * @return AuthResponse con token JWT y datos del usuario
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * INICIAR SESIÓN (LOGIN)
     *
     * POST /auth/login
     *
     * ACCESO: Público (no requiere autenticación)
     *
     * Autentica un usuario con email y contraseña.
     * Si las credenciales son correctas, devuelve un token JWT.
     *
     * Body JSON ejemplo:
     * {
     *   "email": "juan.perez@colegio.es",
     *   "password": "123456"
     * }
     *
     * @param request Email y contraseña (validado con @Valid)
     * @return AuthResponse con token JWT y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    /**
     * OBTENER PERFIL DEL USUARIO AUTENTICADO
     *
     * GET /auth/perfil
     *
     * ACCESO: Requiere autenticación (token JWT)
     *
     * Devuelve los datos del usuario que está logueado.
     * Útil para que el frontend sepa quién es el usuario actual.
     *
     * @param authentication Usuario autenticado (inyectado por Spring Security)
     * @return UsuarioDTO con los datos del usuario
     */
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTO> obtenerPerfil(Authentication authentication) {
        UsuarioDTO usuario = authService.obtenerPerfil(authentication);
        return ResponseEntity.ok(usuario);
    }
}
