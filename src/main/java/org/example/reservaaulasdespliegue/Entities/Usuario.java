package org.example.reservaaulasdespliegue.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ENTIDAD USUARIO
 *
 * Representa un usuario del sistema (profesor o administrador).
 * Implementa UserDetails de Spring Security para la autenticación y autorización.
 *
 * Los usuarios pueden:
 * - Hacer reservas de aulas (profesores y admins)
 * - Gestionar el sistema (solo admins)
 *
 * Roles disponibles:
 * - ROLE_ADMIN: Acceso total al sistema
 * - ROLE_PROFESOR: Acceso limitado, solo puede gestionar sus propias reservas
 */
@Table(name = "usuario") // Nombre de la tabla en la BD
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    // ========== ATRIBUTOS BÁSICOS ==========

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * NOMBRE DEL USUARIO
     *
     * unique = true: No puede haber dos usuarios con el mismo nombre
     * nullable = false: Es obligatorio
     */
    @Column(unique = true, nullable = false)
    private String nombre; // Nombre completo del usuario (ej: "Juan Pérez")

    /**
     * ROL DEL USUARIO
     *
     * Define los permisos del usuario en el sistema.
     * Valores posibles: "ROLE_ADMIN" o "ROLE_PROFESOR"
     *
     * IMPORTANTE: Spring Security requiere que los roles empiecen por "ROLE_"
     */
    @Column(nullable = false)
    private String role; // "ROLE_ADMIN" o "ROLE_PROFESOR"

    /**
     * EMAIL DEL USUARIO
     *
     * Puede usarse para login o notificaciones.
     * unique = true: No puede haber dos usuarios con el mismo email
     */
    @Column(unique = true, nullable = false)
    private String email; // Email del usuario (ej: "juan.perez@colegio.es")

    /**
     * CONTRASEÑA
     *
     * Se guarda ENCRIPTADA (con BCrypt) por seguridad.
     * NUNCA guardar contraseñas en texto plano.
     */
    @Column(nullable = false)
    private String password; // Contraseña encriptada

    /**
     * CUENTA HABILITADA
     *
     * Permite desactivar usuarios sin borrarlos de la BD.
     * Si enabled = false, el usuario no puede hacer login.
     */
    @Column(nullable = false)
    private boolean enabled = true; // Por defecto las cuentas están activas


    // ========== RELACIONES ==========

    /**
     * RELACIÓN ONE-TO-MANY con RESERVA
     *
     * Un usuario puede crear muchas reservas.
     * Cada reserva pertenece a un solo usuario.
     *
     * mappedBy = "usuario": El lado propietario está en Reserva
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Reserva> reservas = new ArrayList<>();


    // ========== MÉTODOS DE USERDETAILS (Spring Security) ==========

    /**
     * OBTENER AUTORIDADES (ROLES)
     *
     * Spring Security usa este método para saber qué permisos tiene el usuario.
     * Convierte el String "ROLE_ADMIN" en un objeto GrantedAuthority.
     *
     * @return Lista de roles del usuario
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * OBTENER NOMBRE DE USUARIO PARA LOGIN
     *
     * Spring Security usa este método para identificar al usuario.
     * En nuestro caso, usamos el EMAIL como nombre de usuario.
     *
     * @return El email del usuario (usado para hacer login)
     */
    @Override
    public String getUsername() {
        return email; // Usamos el email para hacer login
    }

    /**
     * VERIFICAR SI LA CUENTA HA EXPIRADO
     *
     * En este sistema las cuentas no expiran, siempre devolvemos true.
     *
     * @return true (las cuentas no expiran)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Las cuentas nunca expiran
    }

    /**
     * VERIFICAR SI LA CUENTA ESTÁ BLOQUEADA
     *
     * En este sistema no bloqueamos cuentas, siempre devolvemos true.
     *
     * @return true (las cuentas no se bloquean)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Las cuentas nunca se bloquean
    }

    /**
     * VERIFICAR SI LAS CREDENCIALES HAN EXPIRADO
     *
     * En este sistema las contraseñas no expiran, siempre devolvemos true.
     *
     * @return true (las contraseñas no expiran)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las contraseñas nunca expiran
    }

    /**
     * VERIFICAR SI LA CUENTA ESTÁ HABILITADA
     *
     * Usa el campo "enabled" para determinar si el usuario puede hacer login.
     *
     * @return true si la cuenta está activa, false si está deshabilitada
     */
    @Override
    public boolean isEnabled() {
        return enabled; // Devuelve el valor del campo enabled
    }
}
