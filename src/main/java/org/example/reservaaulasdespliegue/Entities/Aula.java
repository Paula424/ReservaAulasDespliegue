package org.example.reservaaulasdespliegue.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD AULA
 *
 * Representa un aula física del centro educativo.
 * Cada aula tiene sus características (nombre, capacidad, si tiene ordenadores...)
 * y puede tener múltiples reservas asociadas.
 *
 * Ejemplo: "Aula 101" con capacidad para 30 alumnos y 15 ordenadores
 */
@Table(name = "aulas") // Nombre de la tabla en la base de datos
@Entity // Indica que es una entidad JPA (se mapeará a una tabla)
@Getter // Lombok genera automáticamente los getters
@Setter // Lombok genera automáticamente los setters
@NoArgsConstructor // Lombok genera un constructor sin parámetros (necesario para JPA)
@AllArgsConstructor // Lombok genera un constructor con todos los parámetros
public class Aula {

    // ========== ATRIBUTOS BÁSICOS ==========

    @Id // Indica que este campo es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento automático
    private Long id;

    @Column(nullable = false) // El nombre es obligatorio
    private String nombre; // Ejemplo: "Aula 101", "Laboratorio de Informática"

    @Column(nullable = false) // La capacidad es obligatoria
    private Integer capacidad; // Número máximo de personas que caben en el aula

    @Column(nullable = false) // Campo obligatorio, por defecto false
    private Boolean esAulaDeOrdenadores = false; // true si tiene ordenadores, false si no

    // El número de ordenadores solo tiene sentido si esAulaDeOrdenadores = true
    private Integer numeroOrdenadores; // Puede ser null si no es aula de ordenadores


    // ========== RELACIONES ==========

    /**
     * RELACIÓN ONE-TO-MANY (Uno a Muchos)
     *
     * Un aula puede tener MUCHAS reservas, pero cada reserva pertenece a UN solo aula.
     *
     * @OneToMany: Indica la relación 1:N
     * mappedBy = "aula": Indica que el lado propietario de la relación está en la clase Reserva,
     *                     en su atributo "aula". Esto evita crear una tabla intermedia innecesaria.
     * cascade = CascadeType.ALL: Si eliminamos el aula, se eliminan automáticamente sus reservas
     * orphanRemoval = true: Si quitamos una reserva de la lista, se borra de la BD
     *
     * @JsonManagedReference: Evita el ciclo infinito al serializar a JSON
     *                        (Aula tiene Reservas, Reserva tiene Aula, Aula tiene Reservas...)
     *                        Esta anotación indica "este es el lado principal de la relación"
     */
    @OneToMany(mappedBy = "aula", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Lado principal de la relación bidireccional
    private List<Reserva> reservas = new ArrayList<>(); // Inicializamos la lista vacía para evitar NullPointer
}