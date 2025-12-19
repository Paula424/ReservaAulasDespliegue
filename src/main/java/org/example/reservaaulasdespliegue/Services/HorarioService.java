package org.example.reservaaulasdespliegue.Services;

import lombok.RequiredArgsConstructor;
import org.example.reservaaulasdespliegue.DTO.HorarioDTO;
import org.example.reservaaulasdespliegue.DTO.HorarioRequest;
import org.example.reservaaulasdespliegue.Entities.Horario;
import org.example.reservaaulasdespliegue.Repositories.HorarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================
 * SERVICIO DE TRAMOS HORARIOS
 * ============================================
 *
 * Gestiona los tramos horarios del centro educativo.
 * Los tramos definen las franjas horarias disponibles para reservar.
 */
@Service
@RequiredArgsConstructor
public class HorarioService {

    private final HorarioRepository horarioRepository;

    /**
     * LISTAR TODOS LOS TRAMOS HORARIOS
     *
     * Devuelve todos los tramos horarios configurados.
     * Endpoint: GET /tramo-horario
     *
     * @return Lista de TramoHorarioDTO
     */
    public List<HorarioDTO> listarTodos() {
        List<Horario> tramos = horarioRepository.findAll();

        return tramos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * OBTENER TRAMO HORARIO POR ID
     *
     * Busca un tramo horario específico.
     * Endpoint: GET /tramo-horario/{id}
     *
     * @param id ID del tramo horario
     * @return TramoHorarioDTO
     */
    public HorarioDTO obtenerPorId(Long id) {
        Horario tramo = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tramo horario con ID " + id + " no encontrado"));

        return convertirADTO(tramo);
    }

    /**
     * CREAR TRAMO HORARIO
     *
     * Crea un nuevo tramo horario.
     * Endpoint: POST /tramo-horario
     * Solo ADMIN puede crear tramos.
     *
     * VALIDACIONES:
     * - No puede existir ya un tramo con el mismo día y sesión
     * - La hora de inicio debe ser anterior a la hora de fin
     *
     * @param request Datos del tramo horario
     * @return TramoHorarioDTO del tramo creado
     */
    @Transactional
    public HorarioDTO crear(HorarioRequest request) {
        // Validar que no exista ya ese tramo (mismo día y sesión)
        horarioRepository.findByDiaSemanaAndSesionDia(
                request.diaSemana(),
                request.sesionDia()
        ).ifPresent(tramo -> {
            throw new RuntimeException(
                    "Ya existe un tramo horario para " + request.diaSemana() +
                            " sesión " + request.sesionDia()
            );
        });

        // Validar que horaInicio < horaFin (esto también se valida en el DTO)
        if (request.horaInicio().isAfter(request.horaFin())) {
            throw new RuntimeException("La hora de inicio debe ser anterior a la hora de fin");
        }

        // Crear el tramo horario
        Horario tramo = new Horario();
        tramo.setDiaSemana(request.diaSemana());
        tramo.setSesionDia(request.sesionDia());
        tramo.setHoraInicio(request.horaInicio());
        tramo.setHoraFin(request.horaFin());
        tramo.setTipo(request.tipo());

        Horario tramoGuardado = horarioRepository.save(tramo);

        return convertirADTO(tramoGuardado);
    }

    /**
     * ELIMINAR TRAMO HORARIO
     *
     * Elimina un tramo horario del sistema.
     * Endpoint: DELETE /tramo-horario/{id}
     * Solo ADMIN puede eliminar tramos.
     *
     * IMPORTANTE: Si el tramo tiene reservas asociadas, se eliminarán también
     * (o puedes añadir validación para no permitirlo)
     *
     * @param id ID del tramo a eliminar
     */
    @Transactional
    public void eliminar(Long id) {
        Horario tramo = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tramo horario no encontrado"));
        if (tramo.getReservas() != null && !tramo.getReservas().isEmpty()) {
            throw new RuntimeException("La reserva no puede ser eliminada");
        }

        horarioRepository.deleteById(id);
    }

    /**
     * LISTAR TRAMOS POR DÍA
     *
     * Devuelve todos los tramos de un día específico.
     * Útil para mostrar el horario de un día completo.
     *
     * @param diaSemana Día de la semana
     * @return Lista de TramoHorarioDTO
     */
    public List<HorarioDTO> listarPorDia(Horario.DiaSemana diaSemana) {
        List<Horario> tramos = horarioRepository.findByDiaSemana(diaSemana);

        return tramos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * CONVERTIR ENTIDAD A DTO
     */
    private HorarioDTO convertirADTO(Horario tramo) {
        return new HorarioDTO(
                tramo.getId(),
                tramo.getDiaSemana(),
                tramo.getSesionDia(),
                tramo.getHoraInicio(),
                tramo.getHoraFin(),
                tramo.getTipo()
        );
    }
}