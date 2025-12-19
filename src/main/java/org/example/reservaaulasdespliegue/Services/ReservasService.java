package org.example.reservaaulasdespliegue.Services;

import lombok.RequiredArgsConstructor;
import org.example.reservaaulasdespliegue.DTO.*;
import org.example.reservaaulasdespliegue.Entities.Aula;
import org.example.reservaaulasdespliegue.Entities.Horario;
import org.example.reservaaulasdespliegue.Entities.Reserva;
import org.example.reservaaulasdespliegue.Entities.Usuario;
import org.example.reservaaulasdespliegue.Repositories.AulaRepository;
import org.example.reservaaulasdespliegue.Repositories.HorarioRepository;
import org.example.reservaaulasdespliegue.Repositories.ReservaRepository;
import org.example.reservaaulasdespliegue.Repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================
 * SERVICIO DE RESERVAS
 * ============================================
 *
 * Contiene toda la lógica de negocio relacionada con reservas:
 * - Crear reservas con validaciones
 * - Listar reservas
 * - Eliminar reservas
 *
 * VALIDACIONES CRÍTICAS:
 * 1. No permitir solapamientos (misma aula, fecha y horario)
 * 2. No permitir reservas en el pasado
 * 3. El número de asistentes no puede superar la capacidad del aula
 */
@Service
@RequiredArgsConstructor
public class ReservasService {

    // Repositorios necesarios
    private final ReservaRepository reservaRepository;
    private final AulaRepository aulaRepository;
    private final HorarioRepository tramoHorarioRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * LISTAR TODAS LAS RESERVAS
     *
     * Devuelve todas las reservas del sistema.
     * Endpoint: GET /reservas
     * Solo accesible para ADMIN.
     *
     * @return Lista de ReservaDTO
     */
    public List<ReservaDTO> listarTodas() {
        List<Reserva> reservas = reservaRepository.findAll();

        return reservas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * OBTENER RESERVA POR ID
     *
     * Busca una reserva específica por su ID.
     * Endpoint: GET /reservas/{id}
     *
     * @param id ID de la reserva
     * @return ReservaDTO con los datos
     * @throws RuntimeException si no existe
     */
    public ReservaDTO obtenerPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva con ID " + id + " no encontrada"));

        return convertirADTO(reserva);
    }

    /**
     * CREAR NUEVA RESERVA
     *
     * Crea una reserva validando todas las reglas de negocio.
     * Endpoint: POST /reservas
     *
     * VALIDACIONES:
     * 1. El aula existe
     * 2. El tramo horario existe
     * 3. La fecha no es pasada
     * 4. No hay solapamiento con otra reserva
     * 5. El número de asistentes no supera la capacidad del aula
     *
     * @param request Datos de la reserva
     * @param authentication Usuario autenticado que crea la reserva
     * @return ReservaDTO de la reserva creada
     */
    @Transactional
    public ReservaDTO crear(ReservaRequest request, Authentication authentication) {
        // 1. BUSCAR Y VALIDAR EL AULA
        Aula aula = aulaRepository.findById(request.aulaId())
                .orElseThrow(() -> new RuntimeException("Aula con ID " + request.aulaId() + " no encontrada"));

        // 2. BUSCAR Y VALIDAR EL TRAMO HORARIO
        Horario horario = null;
        if (request.tramoHorarioId() != null) {
            horario =  tramoHorarioRepository.findById(request.tramoHorarioId())
                    .orElseThrow(() -> new RuntimeException("Tramo horario con ID " + request.tramoHorarioId() + " no encontrado"));
        }

        // 3. BUSCAR EL USUARIO AUTENTICADO
        String email = authentication.getName(); // Email del usuario logueado
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 4. VALIDAR QUE LA FECHA NO SEA PASADA
        if (request.fecha().isBefore(LocalDate.now())) {
            throw new RuntimeException("No se pueden hacer reservas en fechas pasadas");
        }

        // 5. VALIDAR QUE NO HAYA SOLAPAMIENTO
        List<Reserva> reservasExistentes = reservaRepository.findByAulaAndFechaAndTramoHorario(
                aula, request.fecha(), horario
        );

        if (!reservasExistentes.isEmpty()) {
            throw new RuntimeException(
                    "Ya existe una reserva para el aula '" + aula.getNombre() +
                            "' el día " + request.fecha() + " en ese horario"
            );
        }

        // 6. VALIDAR QUE EL NÚMERO DE ASISTENTES NO SUPERE LA CAPACIDAD
        if (request.numeroAsistentes() > aula.getCapacidad()) {
            throw new RuntimeException(
                    "El número de asistentes (" + request.numeroAsistentes() +
                            ") supera la capacidad del aula (" + aula.getCapacidad() + ")"
            );
        }

        // 7. TODO OK - CREAR LA RESERVA
        Reserva reserva = new Reserva();
        reserva.setFecha(request.fecha());
        reserva.setMotivo(request.motivo());
        reserva.setNumeroAsistentes(request.numeroAsistentes());
        reserva.setAula(aula);
        reserva.setTramoHorario(horario);
        reserva.setUsuario(usuario);
        // fechaCreacion se establece automáticamente con @CreationTimestamp

        // 8. GUARDAR EN LA BD
        Reserva reservaGuardada = reservaRepository.save(reserva);

        return convertirADTO(reservaGuardada);
    }

    /**
     * ELIMINAR RESERVA
     *
     * Elimina una reserva del sistema.
     * Endpoint: DELETE /reservas/{id}
     *
     * REGLAS DE AUTORIZACIÓN:
     * - Los ADMIN pueden eliminar cualquier reserva
     * - Los PROFESOR solo pueden eliminar sus propias reservas
     *
     * @param id ID de la reserva a eliminar
     * @param authentication Usuario autenticado
     */
    @Transactional
    public void eliminar(Long id, Authentication authentication) {
        // Buscar la reserva
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva con ID " + id + " no encontrada"));

        // Obtener el usuario autenticado
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar permisos
        boolean esAdmin = usuario.getRole().equals("ROLE_ADMIN");
        boolean esSuReserva = reserva.getUsuario().getId().equals(usuario.getId());

        if (!esAdmin && !esSuReserva) {
            throw new RuntimeException(
                    "No tienes permiso para eliminar esta reserva. " +
                            "Solo puedes eliminar tus propias reservas."
            );
        }

        // Eliminar la reserva
        reservaRepository.deleteById(id);
    }

    /**
     * LISTAR RESERVAS DE UN USUARIO
     *
     * Devuelve todas las reservas creadas por un usuario específico.
     * Útil para que un profesor vea sus propias reservas.
     * Endpoint: GET /reservas/usuario/{usuarioId} (o GET /reservas/mis-reservas)
     *
     * @param usuarioId ID del usuario
     * @return Lista de ReservaDTO
     */
    public List<ReservaDTO> listarPorUsuario(Long usuarioId) {
        List<Reserva> reservas = reservaRepository.findByUsuarioId(usuarioId);

        return reservas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * LISTAR MIS RESERVAS (usuario autenticado)
     *
     * Devuelve las reservas del usuario que está logueado.
     * Endpoint: GET /reservas/mis-reservas
     *
     * @param authentication Usuario autenticado
     * @return Lista de ReservaDTO
     */
    public List<ReservaDTO> listarMisReservas(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Reserva> reservas = reservaRepository.findByUsuario(usuario);

        return reservas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * CONVERTIR ENTIDAD RESERVA A DTO
     */
    private ReservaDTO convertirADTO(Reserva reserva) {
        // Crear DTOs anidados
        AulaDTO aulaDTO = new AulaDTO(
                reserva.getAula().getId(),
                reserva.getAula().getNombre(),
                reserva.getAula().getCapacidad(),
                reserva.getAula().getEsAulaDeOrdenadores(),
                reserva.getAula().getNumeroOrdenadores()
        );

        HorarioDTO tramoDTO = null;
        if (reserva.getTramoHorario() != null) {
            tramoDTO = new HorarioDTO(
                    reserva.getTramoHorario().getId(),
                    reserva.getTramoHorario().getDiaSemana(),
                    reserva.getTramoHorario().getSesionDia(),
                    reserva.getTramoHorario().getHoraInicio(),
                    reserva.getTramoHorario().getHoraFin(),
                    reserva.getTramoHorario().getTipo()
            );
        }



        UsuarioDTO usuarioDTO = new UsuarioDTO(
                reserva.getUsuario().getId(),
                reserva.getUsuario().getNombre(),
                reserva.getUsuario().getEmail(),
                reserva.getUsuario().getRole(),
                reserva.getUsuario().isEnabled()
        );

        return new ReservaDTO(
                reserva.getId(),
                reserva.getFecha(),
                reserva.getMotivo(),
                reserva.getNumeroAsistentes(),
                reserva.getFechaCreacion(),
                aulaDTO,
                tramoDTO,
                usuarioDTO
        );
    }
}