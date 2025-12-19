package org.example.reservaaulasdespliegue.Services;

import lombok.RequiredArgsConstructor;
import org.example.reservaaulasdespliegue.Repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ============================================
 * SERVICIO USER DETAILS (Spring Security)
 * ============================================
 *
 * Este servicio es CRÍTICO para Spring Security.
 * Spring lo usa para cargar los datos del usuario desde la BD
 * cuando alguien intenta hacer login.
 *
 * Implementa UserDetailsService de Spring Security.
 */
@Service
@RequiredArgsConstructor // Lombok genera el constructor con el repositorio inyectado
public class UsuarioDetailService implements UserDetailsService {

    // Repositorio para buscar usuarios en la BD
    private final UsuarioRepository usuarioRepository;

    /**
     * CARGAR USUARIO POR EMAIL
     *
     * Spring Security llama a este método automáticamente durante el login.
     *
     * Flujo:
     * 1. Usuario envía email + password en POST /auth/login
     * 2. Spring Security llama a este método con el email
     * 3. Buscamos el usuario en la BD
     * 4. Si existe, Spring Security compara la password
     * 5. Si coincide, el login es exitoso
     *
     * @param email Email del usuario (username en Spring Security)
     * @return UserDetails con los datos del usuario
     * @throws UsernameNotFoundException si el usuario no existe
     *
     * IMPORTANTE: Nuestra entidad Usuario implementa UserDetails,
     * por eso podemos devolverla directamente.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Buscar usuario por email en la BD
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario con email " + email + " no encontrado"
                ));
    }
}