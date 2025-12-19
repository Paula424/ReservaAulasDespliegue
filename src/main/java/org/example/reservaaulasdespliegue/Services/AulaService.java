package org.example.reservaaulasdespliegue.Services;

import lombok.RequiredArgsConstructor;
import org.example.reservaaulasdespliegue.DTO.*;
import org.example.reservaaulasdespliegue.Entities.Aula;
import org.example.reservaaulasdespliegue.Entities.Reserva;
import org.example.reservaaulasdespliegue.Repositories.AulaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================
 * SERVICIO DE AULAS
 * ============================================
 *
 * Contiene toda la lógica de negocio relacionada con las aulas:
 * - Crear, actualizar, eliminar aulas
 * - Listar aulas con filtros
 * - Consultar reservas de un aula
 *
 * @Transactional: Las operaciones de escritura son transaccionales
 * (si algo falla, se hace rollback automático)
 */
@Service
@RequiredArgsConstructor // Inyección de dependencias por constructor
public class AulaService {

    // Repositorio para acceder a la base de datos
    private final AulaRepository aulaRepository;

    /**
     * LISTAR TODAS LAS AULAS
     *
     * Devuelve todas las aulas del sistema.
     * Endpoint: GET /aulas
     *
     * @return Lista de AulaDTO
     */
    public List<AulaDTO> listarTodas() {
        // Obtener todas las aulas de la BD
        List<Aula> aulas = aulaRepository.findAll();

        // Convertir cada Aula a AulaDTO usando streams
        return aulas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * OBTENER AULA POR ID
     *
     * Busca un aula específica por su ID.
     * Endpoint: GET /aulas/{id}
     *
     * @param id ID del aula
     * @return AulaDTO con los datos del aula
     * @throws RuntimeException si el aula no existe
     */
    public AulaDTO obtenerPorId(Long id) {
        // Buscar el aula, lanzar excepción si no existe
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula con ID " + id + " no encontrada"));

        return convertirADTO(aula);
    }

    /**
     * CREAR NUEVA AULA
     *
     * Crea un aula nueva en el sistema.
     * Endpoint: POST /aulas
     *
     * Validaciones:
     * - Si es aula de ordenadores, debe tener al menos 1 ordenador
     * - Si NO es aula de ordenadores, el número de ordenadores debe ser 0 o null
     *
     * @param request Datos del aula a crear
     * @return AulaDTO del aula creada
     */
    @Transactional // Operación transaccional (rollback automático si falla)
    public AulaDTO crear(AulaRequest request) {
        // Validar lógica de ordenadores
        validarOrdenadores(request);

        // Crear entidad Aula desde el DTO
        Aula aula = new Aula();
        aula.setNombre(request.nombre());
        aula.setCapacidad(request.capacidad());
        aula.setEsAulaDeOrdenadores(request.esAulaDeOrdenadores());
        aula.setNumeroOrdenadores(request.numeroOrdenadores());

        // Guardar en la base de datos
        Aula aulaGuardada = aulaRepository.save(aula);

        return convertirADTO(aulaGuardada);
    }

    /**
     * ACTUALIZAR AULA
     *
     * Modifica los datos de un aula existente.
     * Endpoint: PUT /aulas/{id}
     *
     * @param id ID del aula a actualizar
     * @param request Nuevos datos del aula
     * @return AulaDTO con los datos actualizados
     */
    @Transactional
    public AulaDTO actualizar(Long id, AulaRequest request) {
        // Buscar el aula existente
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula con ID " + id + " no encontrada"));

        // Validar lógica de ordenadores
        validarOrdenadores(request);

        // Actualizar los campos
        aula.setNombre(request.nombre());
        aula.setCapacidad(request.capacidad());
        aula.setEsAulaDeOrdenadores(request.esAulaDeOrdenadores());
        aula.setNumeroOrdenadores(request.numeroOrdenadores());

        // Guardar cambios
        Aula aulaActualizada = aulaRepository.save(aula);

        return convertirADTO(aulaActualizada);
    }

    /**
     * ELIMINAR AULA
     *
     * Elimina un aula del sistema.
     * Endpoint: DELETE /aulas/{id}
     *
     * IMPORTANTE: Si el aula tiene reservas, se eliminarán también
     * (por cascade = CascadeType.ALL en la entidad Aula)
     *
     * @param id ID del aula a eliminar
     */
    @Transactional
    public void eliminar(Long id) {
        // Verificar que existe
        if (!aulaRepository.existsById(id)) {
            throw new RuntimeException("Aula con ID " + id + " no encontrada");
        }

        // Eliminar el aula (y sus reservas en cascada)
        aulaRepository.deleteById(id);
    }

    /**
     * LISTAR RESERVAS DE UN AULA
     *
     * Devuelve todas las reservas de un aula específica.
     * Endpoint: GET /aulas/{id}/reservas
     *
     * @param id ID del aula
     * @return AulaConReservasDTO con el aula y sus reservas
     */
    public AulaConReservasDTO obtenerConReservas(Long id) {
        // Buscar el aula
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula con ID " + id + " no encontrada"));

        // Convertir las reservas a DTOs
        List<ReservaDTO> reservasDTO = aula.getReservas().stream()
                .map(this::convertirReservaADTO)
                .collect(Collectors.toList());

        // Crear y devolver el DTO completo
        return new AulaConReservasDTO(
                aula.getId(),
                aula.getNombre(),
                aula.getCapacidad(),
                aula.getEsAulaDeOrdenadores(),
                aula.getNumeroOrdenadores(),
                reservasDTO
        );
    }

    /**
     * BUSCAR AULAS POR CAPACIDAD MÍNIMA
     *
     * Filtra aulas que tengan al menos X capacidad.
     * Endpoint: GET /aulas?capacidad=20
     *
     * @param capacidad Capacidad mínima
     * @return Lista de AulaDTO que cumplen el criterio
     */
    public List<AulaDTO> buscarPorCapacidad(Integer capacidad) {
        List<Aula> aulas = aulaRepository.findByCapacidadGreaterThanEqual(capacidad);

        return aulas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * BUSCAR AULAS CON ORDENADORES
     *
     * Filtra aulas que tienen ordenadores.
     * Endpoint: GET /aulas?ordenadores=true
     *
     * @return Lista de AulaDTO de aulas con ordenadores
     */
    public List<AulaDTO> buscarConOrdenadores() {
        List<Aula> aulas = aulaRepository.findByEsAulaDeOrdenadoresTrue();

        return aulas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * VALIDAR LÓGICA DE ORDENADORES
     *
     * Verifica que los datos de ordenadores sean coherentes:
     * - Si esAulaDeOrdenadores = true, debe tener al menos 1 ordenador
     * - Si esAulaDeOrdenadores = false, numeroOrdenadores debe ser 0 o null
     */
    private void validarOrdenadores(AulaRequest request) {
        if (request.esAulaDeOrdenadores()) {
            // Es aula de ordenadores: debe tener al menos 1
            if (request.numeroOrdenadores() == null || request.numeroOrdenadores() < 1) {
                throw new RuntimeException(
                        "Un aula de ordenadores debe tener al menos 1 ordenador"
                );
            }
        } else {
            // NO es aula de ordenadores: no puede tener ordenadores
            if (request.numeroOrdenadores() != null && request.numeroOrdenadores() > 0) {
                throw new RuntimeException(
                        "Un aula sin ordenadores no puede tener número de ordenadores mayor a 0"
                );
            }
        }
    }

    /**
     * CONVERTIR ENTIDAD AULA A DTO
     */
    private AulaDTO convertirADTO(Aula aula) {
        return new AulaDTO(
                aula.getId(),
                aula.getNombre(),
                aula.getCapacidad(),
                aula.getEsAulaDeOrdenadores(),
                aula.getNumeroOrdenadores()
        );
    }

    /**
     * CONVERTIR ENTIDAD RESERVA A DTO (versión simplificada)
     */
    private ReservaDTO convertirReservaADTO(Reserva reserva) {
        // Crear DTOs anidados
        AulaDTO aulaDTO = convertirADTO(reserva.getAula());

        HorarioDTO tramoDTO = new HorarioDTO(
                reserva.getTramoHorario().getId(),
                reserva.getTramoHorario().getDiaSemana(),
                reserva.getTramoHorario().getSesionDia(),
                reserva.getTramoHorario().getHoraInicio(),
                reserva.getTramoHorario().getHoraFin(),
                reserva.getTramoHorario().getTipo()
        );

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

