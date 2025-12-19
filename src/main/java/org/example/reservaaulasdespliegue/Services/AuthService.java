package org.example.reservaaulasdespliegue.Services;

import lombok.RequiredArgsConstructor;
import org.example.reservaaulasdespliegue.DTO.AuthResponse;
import org.example.reservaaulasdespliegue.DTO.LoginRequest;
import org.example.reservaaulasdespliegue.DTO.RegisterRequest;
import org.example.reservaaulasdespliegue.DTO.UsuarioDTO;
import org.example.reservaaulasdespliegue.Entities.Usuario;
import org.example.reservaaulasdespliegue.Repositories.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ============================================
 * SERVICIO DE AUTENTICACIÓN
 * ============================================
 *
 * Gestiona el registro y login de usuarios.
 * Genera tokens JWT para la autenticación.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // Para encriptar contraseñas
    private final AuthenticationManager authenticationManager; // Para autenticar
    private final JwtService jwtService; // Para generar tokens

    /**
     * REGISTRAR NUEVO USUARIO
     *
     * Crea una cuenta nueva en el sistema.
     * Endpoint: POST /auth/register
     *
     * VALIDACIONES:
     * - El email no debe estar ya registrado
     * - El nombre no debe estar ya registrado
     * - La contraseña se encripta antes de guardarla
     *
     * @param request Datos del nuevo usuario
     * @return AuthResponse con token y datos del usuario
     */
    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RuntimeException("El email " + request.email() + " ya está registrado");
        }
        if (usuarioRepository.existsByNombre(request.nombre())) {
            throw new RuntimeException("El nombre " + request.nombre() + " ya está en uso");
        }
        if (!request.role().equals("ROLE_ADMIN") && !request.role().equals("ROLE_PROFESOR")) {
            throw new RuntimeException("El rol debe ser ROLE_ADMIN o ROLE_PROFESOR");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password())); // encriptar
        usuario.setRole(request.role());
        usuario.setEnabled(true);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Autenticar para generar el token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String token = jwtService.generateToken(authentication);

        return new AuthResponse(token, convertirADTO(usuarioGuardado));
    }

    /**
     * LOGIN (INICIAR SESIÓN)
     *
     * Autentica un usuario y genera un token JWT.
     * Endpoint: POST /auth/login
     *
     * @param request Email y contraseña
     * @return AuthResponse con token y datos del usuario
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String token = jwtService.generateToken(authentication);
        System.out.println("Token generado: " + token);

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.isEnabled()) {
            throw new RuntimeException("El usuario está deshabilitado");
        }

        UsuarioDTO dto = convertirADTO(usuario);
        System.out.println("Usuario DTO: " + dto);

        return new AuthResponse(token, dto);
    }
    /**
     * OBTENER PERFIL DEL USUARIO AUTENTICADO
     *
     * Devuelve los datos del usuario que está logueado.
     * Endpoint: GET /auth/perfil
     *
     * @param authentication Usuario autenticado
     * @return UsuarioDTO con los datos
     */
    public UsuarioDTO obtenerPerfil(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return convertirADTO(usuario);
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

