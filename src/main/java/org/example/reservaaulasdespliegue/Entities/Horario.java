package org.example.reservaaulasdespliegue.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * * Representa una franja horaria específica en el horario escolar.
 *  * Define cuándo ocurre algo: qué día, qué sesión, a qué hora empieza y termina.
 *  *
 *  * Ejemplo: "Lunes, 3ª sesión, de 10:30 a 11:25, tipo LECTIVA"
 *  *
 *  * Los tramos horarios son reutilizables:
 *  * - "Lunes 3ª hora" es el MISMO tramo para todas las semanas del curso
 *  * - Las reservas específicas (con fecha concreta) referencian a estos tramos
 */
@Table(name = "horarios")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Horario {

    // ========== ATRIBUTOS BÁSICOS ==========

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * DÍA DE LA SEMANA
     *
     * Usamos un ENUM para limitar los valores posibles.
     * Solo días lectivos (no hay sábado ni domingo).
     *
     * @Enumerated(EnumType.STRING): Guarda el texto "LUNES" en vez del número (más legible)
     */
    @Enumerated(EnumType.STRING) // Guarda "LUNES", "MARTES"... en la BD
    @Column(nullable = false)
    private DiaSemana diaSemana; // Qué día de la semana es este tramo

    /**
     * NÚMERO DE SESIÓN
     *
     * Indica qué hora del día es (1ª, 2ª, 3ª, 4ª, 5ª, 6ª...)
     * Ejemplo: 1 = primera hora, 2 = segunda hora, etc.
     */
    @Column(nullable = false)
    private Integer sesionDia; // 1, 2, 3, 4, 5, 6...

    /**
     * HORAS DE INICIO Y FIN
     *
     * Definen exactamente cuándo empieza y termina este tramo.
     * Ejemplo: horaInicio = 08:00, horaFin = 08:55
     */
    @Column(nullable = false)
    private LocalTime horaInicio; // Ejemplo: 08:00

    @Column(nullable = false)
    private LocalTime horaFin; // Ejemplo: 08:55

    /**
     * TIPO DE TRAMO
     *
     * Clasifica si es hora de clase, recreo, comedor...
     * Útil para filtrar o aplicar reglas específicas.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTramo tipo; // LECTIVA, RECREO, MEDIODIA


// ========== RELACIONES ==========

    /**
     * RELACIÓN ONE-TO-MANY con RESERVA
     *
     * Un tramo horario puede tener muchas reservas en diferentes fechas.
     * Ejemplo: "Lunes 3ª hora" puede estar reservado el 10/05, el 17/05, el 24/05...
     *
     * mappedBy = "horarios": El lado propietario está en Reserva
     */
    @OneToMany(mappedBy = "tramoHorario")
    @JsonManagedReference // Lado principal para evitar ciclos JSON
    private List<Reserva> reservas = new ArrayList<>();


    // ========== ENUMS INTERNOS ==========

    /**
     * ENUM DÍA DE LA SEMANA
     *
     * Define los días laborables del centro educativo.
     * No incluye sábado ni domingo (no son lectivos).
     */
    public enum DiaSemana {
        LUNES,
        MARTES,
        MIERCOLES,  // Corregido: antes estaba sin tilde pero con tilde queda mejor
        JUEVES,
        VIERNES
    }

    /**
     * ENUM TIPO DE TRAMO
     *
     * Clasifica el tipo de actividad del tramo horario.
     * - LECTIVA: Hora de clase normal
     * - RECREO: Descanso/patio
     * - MEDIODIA: Hora del comedor
     */
    public enum TipoTramo {
        LECTIVA,     // Hora de clase
        RECREO,      // Descanso
        MEDIODIA     // Hora del comedor
    }
}