package org.example.reservaaulasdespliegue.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * ============================================
 * SERVICIO JWT (JSON Web Token)
 * ============================================
 *
 * Este servicio maneja todo lo relacionado con tokens JWT:
 * - Generar tokens cuando un usuario hace login
 * - Validar tokens en cada petición
 * - Extraer información del token (email, roles)
 *
 * ¿Qué es un JWT?
 * Es un "ticket" encriptado que contiene información del usuario.
 * El cliente lo guarda y lo envía en cada petición para autenticarse.
 */
@Service
public class JwtService {

    // Clave secreta para firmar los tokens (muy importante, debe ser segura)
    private final SecretKey secretKey;

    // Tiempo de expiración del token (24 horas en milisegundos)
    private static final long EXPIRATION_TIME = 86400000; // 24h = 24 * 60 * 60 * 1000
    private static final String SECRET_KEY = "xsjc7jKebsyGtFs2ATK1to1iTe6hFosACLIWET/IM3g=";


    /**
     * CONSTRUCTOR
     *
     * Genera automáticamente una clave secreta segura de 256 bits.
     * IMPORTANTE: En producción deberías cargar esta clave desde variables de entorno
     * o un archivo de configuración, no generarla cada vez que arranca la app.
     * Convierte esa cadena en una clave segura que libreria jjwt puede usar para firnar tokens
     */
    public JwtService() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);}

    /**
     * GENERAR TOKEN JWT
     *
     * Crea un token JWT cuando un usuario hace login exitosamente.
     * El token contiene:
     * - Email del usuario (subject)
     * - Roles del usuario (claim "roles")
     * - Fecha de creación y expiración
     * - Firma para evitar manipulación
     *
     * @param authentication Datos del usuario autenticado
     * @return Token JWT en formato String
     *
     * Ejemplo de token generado:
     * "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqdWFuQGNvbGVnaW8uZXMi..."
     */
    public String generateToken(Authentication authentication) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String token = Jwts.builder()
                .subject(authentication.getName())
                .issuer("reservas-aulas-api")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("roles", roles)
                .signWith(secretKey)
                .compact();

        System.out.println("Token generado: " + token);
        return token;
    }

    /**
     * EXTRAER EMAIL DEL TOKEN
     *
     * Obtiene el email (subject) desde un token JWT.
     * Útil para saber qué usuario está haciendo la petición.
     *
     * @param token Token JWT
     * @return Email del usuario
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * EXTRAER ROLES DEL TOKEN
     *
     * Obtiene los roles desde un token JWT.
     * Útil para verificar permisos sin consultar la base de datos.
     *
     * @param token Token JWT
     * @return Roles del usuario (ej: "ROLE_PROFESOR")
     */
    public String extractRoles(String token) {
        return extractClaims(token).get("roles", String.class);
    }

    /**
     * VALIDAR TOKEN
     *
     * Verifica si un token es válido:
     * - La firma es correcta (no ha sido modificado)
     * - No ha expirado
     * - El email coincide con el usuario
     *
     * @param token Token a validar
     * @param email Email del usuario que debería tener el token
     * @return true si el token es válido, false si no
     */
    public boolean isTokenValid(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }

    /**
     * VERIFICAR SI EL TOKEN HA EXPIRADO
     *
     * @param token Token JWT
     * @return true si expiró, false si aún es válido
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    /**
     * EXTRAER TODA LA INFORMACIÓN DEL TOKEN
     *
     * Parsea el token y extrae todos los claims (información) que contiene.
     *
     * @param token Token JWT
     * @return Claims (toda la info del token)
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // Verificar con la clave secreta
                .build()
                .parseSignedClaims(token) // Parsear el token
                .getPayload(); // Obtener el contenido
    }

    /**
     * OBTENER LA CLAVE SECRETA
     *
     * Devuelve la clave secreta para que otros componentes
     * (como el filtro JWT) puedan validar tokens.
     *
     * @return Clave secreta
     */
    public SecretKey getSecretKey() {
        return secretKey;
    }
}
