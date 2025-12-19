package org.example.reservaaulasdespliegue.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

/**
 * ENTIDAD RESERVA
 *
 * Representa una reserva de un aula en una fecha y horario específicos.
 * Cada reserva está asociada a:
 * - Un aula específica
 * - Un  horario (sesión del día)
 * - Un usuario que la creó
 *
 * Ejemplo: "Reunión de departamento el 20/05/2025 en Aula 101 durante la 3ª hora"
 */
@Table(name = "reservas") // Nombre de la tabla en la BD
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    // ========== ATRIBUTOS BÁSICOS ==========

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // La fecha es obligatoria
    private LocalDate fecha; // Fecha en la que se hará la reserva (ej: 2025-05-20)

    @Column(nullable = false, length = 500) // El motivo es obligatorio, máximo 500 caracteres
    private String motivo; // Descripción de para qué es la reserva (ej: "Reunión departamento")

    @Column(nullable = false) // Número de asistentes obligatorio
    private Integer numeroAsistentes; // Cuánta gente asistirá (debe ser <= capacidad del aula)

    /**
     * FECHA DE CREACIÓN AUTOMÁTICA
     *
     * @CreationTimestamp: Hibernate inserta automáticamente la fecha actual al crear la reserva
     * No hace falta asignarla manualmente en el código
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false) // No se puede modificar después de crearse
    private LocalDate fechaCreacion; // Cuándo se creó esta reserva en el sistema


    // ========== RELACIONES ==========

    /**
     * RELACIÓN MANY-TO-ONE con AULA
     *
     * Muchas reservas pueden pertenecer a la misma aula.
     * Cada reserva solo puede ser de UN aula.
     *
     * @ManyToOne: Indica que es el lado "muchos" de la relación
     * @JoinColumn: Crea la columna "aula_id" en la tabla reservas que guarda el id del aula
     * @JsonBackReference: Complementa al @JsonManagedReference de Aula
     *                     Indica "este es el lado secundario, no lo serialices en JSON"
     *                     para evitar ciclos infinitos
     */
    @ManyToOne
    @JoinColumn(name = "aula_id", nullable = false) // La reserva DEBE tener un aula
    @JsonBackReference // Lado secundario de la relación bidireccional
    private Aula aula; // El aula que se está reservando
    /**
     * RELACIÓN MANY-TO-ONE con  HORARIO
     *
     * Muchas reservas pueden usar el mismo  horario.
     * Ejemplo: Varias reservas diferentes pueden ser todas en "Lunes 3ª hora"
     */
    @ManyToOne
    @JoinColumn(name = "horario_id", nullable = false) // La reserva DEBE tener un horario
    private Horario tramoHorario; // El horario de la reserva (día de la semana + sesión)

    /**
     * RELACIÓN MANY-TO-ONE con USUARIO
     *
     * Muchas reservas pueden ser creadas por el mismo usuario.
     * Cada reserva tiene UN único usuario que la creó.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) // La reserva DEBE tener un usuario
    private Usuario usuario; // Quién hizo esta reserva


    /**
     * MÉTODO PRE-PERSIST
     *
     * Este método se ejecuta automáticamente ANTES de guardar la entidad en la BD.
     * Lo usamos como respaldo por si @CreationTimestamp no funciona.
     *
     * @PrePersist es un callback del ciclo de vida de JPA
     */
    @PrePersist
    public void prePersist() {
        // Si la fecha de creación no se ha establecido, la ponemos ahora
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDate.now();
        }
    }
}