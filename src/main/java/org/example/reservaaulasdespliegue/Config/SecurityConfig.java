package org.example.reservaaulasdespliegue.Config;

import lombok.RequiredArgsConstructor;
import org.example.reservaaulasdespliegue.Services.JwtService;
import org.example.reservaaulasdespliegue.Services.UsuarioDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ============================================
 * CONFIGURACIÓN DE SEGURIDAD
 * ============================================
 *
 * Esta clase configura Spring Security para proteger la API REST.
 *
 * CARACTERÍSTICAS:
 * - Autenticación con JWT (JSON Web Tokens)
 * - Sin sesiones (stateless) - cada petición lleva su token
 * - Control de acceso basado en roles (ADMIN, PROFESOR)
 * - Encriptación de contraseñas con BCrypt
 *
 * FLUJO DE AUTENTICACIÓN:
 * 1. Usuario hace POST /auth/login con email y password
 * 2. Si las credenciales son correctas, recibe un token JWT
 * 3. En cada petición posterior, envía el token en el header:
 *    Authorization: Bearer <token>
 * 4. Spring Security valida el token automáticamente
 * 5. Si el token es válido, permite el acceso (según los permisos)
 */
@Configuration
@EnableWebSecurity // Habilita Spring Security
@EnableMethodSecurity // Permite usar @PreAuthorize en los controladores
@RequiredArgsConstructor
public class SecurityConfig {
        // Servicio para generar y validar tokens JWT
        private final JwtService jwtService;
        private final UsuarioDetailService usuarioDetailService;

        /**
         * CADENA DE FILTROS DE SEGURIDAD
         *
         * Configura cómo Spring Security protege la aplicación.
         * Define qué rutas son públicas y cuáles requieren autenticación.
         *
         * @param http Configurador de seguridad HTTP
         * @return SecurityFilterChain configurada
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/auth/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                    )
                    .userDetailsService(usuarioDetailService)
                    .build();
        }


        /**
         * DECODIFICADOR DE JWT
         *
         * Configura cómo validar los tokens JWT.
         * Usa la clave secreta del JwtService para verificar la firma.
         *
         * IMPORTANTE: La misma clave que se usó para FIRMAR el token
         * debe usarse para VALIDARLO.
         *
         * @return JwtDecoder configurado
         */
        @Bean
        public JwtDecoder jwtDecoder() {
            // Crea un decoder usando la clave secreta de JwtService
            // Verificará que el token no ha sido manipulado
            return NimbusJwtDecoder
                    .withSecretKey(jwtService.getSecretKey())
                    .build();
        }

        /**
         * CONVERSOR DE AUTENTICACIÓN JWT
         *
         * Configura cómo extraer los ROLES del token JWT.
         * Los roles se guardan en el claim "roles" del token.
         *
         * Ejemplo de token decodificado:
         * {
         *   "sub": "juan@colegio.es",
         *   "roles": "ROLE_PROFESOR",
         *   "iat": 1234567890,
         *   "exp": 1234654290
         * }
         *
         * @return JwtAuthenticationConverter configurado
         */
        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
            // Configurar cómo extraer los roles
            JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

            // El claim del token donde están los roles
            authoritiesConverter.setAuthoritiesClaimName("roles");

            // Sin prefijo adicional (ya vienen como "ROLE_ADMIN", "ROLE_PROFESOR")
            authoritiesConverter.setAuthorityPrefix("");

            // Crear el conversor
            JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
            jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

            return jwtConverter;
        }

        /**
         * ENCRIPTADOR DE CONTRASEÑAS
         *
         * BCrypt es un algoritmo de hash seguro para contraseñas.
         *
         * CARACTERÍSTICAS:
         * - Lento intencionalmente (protege contra ataques de fuerza bruta)
         * - Añade "salt" automáticamente (evita rainbow tables)
         * - Cada hash es único aunque la contraseña sea la misma
         *
         * Ejemplo:
         * password "123456" → hash "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
         *
         * @return PasswordEncoder de BCrypt
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
        /**
         * GESTOR DE AUTENTICACIÓN
         *
         * Necesario para el LOGIN.
         * Valida que el email y password sean correctos.
         *
         * FLUJO:
         * 1. Usuario envía email + password
         * 2. AuthenticationManager busca el usuario (con UsuarioDetailsService)
         * 3. Compara la password (con PasswordEncoder)
         * 4. Si coincide, la autenticación es exitosa
         * 5. Se genera un token JWT
         *
         * @param authConfig Configuración de autenticación de Spring
         * @return AuthenticationManager
         */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
            return authConfig.getAuthenticationManager();
        }
    }

