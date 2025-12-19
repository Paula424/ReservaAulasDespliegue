package org.example.reservaaulasdespliegue.Exceptions;

import org.example.reservaaulasdespliegue.DTO.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================
 * GESTOR GLOBAL DE EXCEPCIONES
 * ============================================
 *
 * Esta clase captura TODAS las excepciones que ocurren en los controladores
 * y las convierte en respuestas JSON consistentes y amigables.
 *
 * ¿POR QUÉ ES IMPORTANTE?
 * Sin este gestor, cuando ocurre un error, Spring devuelve respuestas
 * genéricas difíciles de entender para el cliente (frontend).
 *
 * Con este gestor, todas las respuestas de error tienen el mismo formato:
 * {
 *   "timestamp": "2025-05-20T10:30:00",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "mensaje": "Descripción clara del error",
 *   "path": "/api/reservas"
 * }
 *
 * @RestControllerAdvice: Indica que esta clase maneja excepciones
 *                        para TODOS los controladores REST
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ==========================================
     * ERRORES DE VALIDACIÓN (@Valid)
     * ==========================================
     *
     * Se lanza cuando fallan las validaciones de los DTOs.
     * Ejemplo: @NotBlank, @Email, @Min, etc.
     *
     * Captura: MethodArgumentNotValidException
     *
     * Respuesta ejemplo:
     * {
     *   "timestamp": "2025-05-20T10:30:00",
     *   "status": 400,
     *   "error": "Validation Failed",
     *   "mensaje": "Errores de validación en los datos enviados",
     *   "path": "/reservas",
     *   "errores": [
     *     { "campo": "fecha", "mensaje": "La fecha debe ser futura" },
     *     { "campo": "numeroAsistentes", "mensaje": "El número de asistentes es obligatorio" }
     *   ]
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        // Extraer todos los errores de validación
        List<FieldError> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldError(
                        error.getField(),           // Nombre del campo que falló
                        error.getDefaultMessage()   // Mensaje de error
                ))
                .collect(Collectors.toList());

        // Crear respuesta estructurada
        ValidationErrorResponse response = new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Errores de validación en los datos enviados",
                request.getDescription(false).replace("uri=", ""),
                errores
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * ==========================================
     * CREDENCIALES INCORRECTAS (LOGIN)
     * ==========================================
     *
     * Se lanza cuando el usuario intenta hacer login con
     * email o contraseña incorrectos.
     *
     * Captura: BadCredentialsException
     *
     * Respuesta ejemplo:
     * {
     *   "timestamp": "2025-05-20T10:30:00",
     *   "status": 401,
     *   "error": "Unauthorized",
     *   "mensaje": "Email o contraseña incorrectos",
     *   "path": "/auth/login"
     * }
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            WebRequest request) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Email o contraseña incorrectos",
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * ==========================================
     * USUARIO NO ENCONTRADO
     * ==========================================
     *
     * Se lanza cuando se busca un usuario por email y no existe.
     * Usado principalmente en el login.
     *
     * Captura: UsernameNotFoundException
     *
     * Respuesta ejemplo:
     * {
     *   "timestamp": "2025-05-20T10:30:00",
     *   "status": 404,
     *   "error": "Not Found",
     *   "mensaje": "Usuario no encontrado",
     *   "path": "/auth/login"
     * }
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(
            UsernameNotFoundException ex,
            WebRequest request) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * ==========================================
     * ERRORES DE LÓGICA DE NEGOCIO
     * ==========================================
     *
     * Se lanza cuando se viola alguna regla de negocio en los servicios.
     * Ejemplos:
     * - "Ya existe una reserva en ese horario" (solapamiento)
     * - "El número de asistentes supera la capacidad del aula"
     * - "No se permiten reservas en el pasado"
     * - "El email ya está registrado"
     * - "No tienes permiso para eliminar esta reserva"
     *
     * Captura: RuntimeException
     *
     * Respuesta ejemplo:
     * {
     *   "timestamp": "2025-05-20T10:30:00",
     *   "status": 400,
     *   "error": "Bad Request",
     *   "mensaje": "El número de asistentes (35) supera la capacidad del aula (30)",
     *   "path": "/reservas"
     * }
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            WebRequest request) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * ==========================================
     * ARGUMENTO ILEGAL
     * ==========================================
     *
     * Se lanza cuando se pasan argumentos inválidos a métodos.
     * Ejemplo: En TramoHorarioRequest si horaInicio > horaFin
     *
     * Captura: IllegalArgumentException
     *
     * Respuesta ejemplo:
     * {
     *   "timestamp": "2025-05-20T10:30:00",
     *   "status": 400,
     *   "error": "Bad Request",
     *   "mensaje": "La hora de inicio debe ser anterior a la hora de fin",
     *   "path": "/tramo-horario"
     * }
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * ==========================================
     * ACCESO DENEGADO
     * ==========================================
     *
     * Se lanza cuando un usuario intenta acceder a un recurso
     * para el cual no tiene permisos.
     *
     * Ejemplo: Un PROFESOR intenta eliminar un usuario
     *
     * Captura: org.springframework.security.access.AccessDeniedException
     *
     * Respuesta ejemplo:
     * {
     *   "timestamp": "2025-05-20T10:30:00",
     *   "status": 403,
     *   "error": "Forbidden",
     *   "mensaje": "No tienes permisos para acceder a este recurso",
     *   "path": "/usuario/1"
     * }
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex,
            WebRequest request) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "No tienes permisos para acceder a este recurso",
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * ==========================================
     * CUALQUIER OTRA EXCEPCIÓN NO CAPTURADA
     * ==========================================
     *
     * Captura cualquier error inesperado que no haya sido
     * manejado por los handlers específicos anteriores.
     *
     * Es el "catch-all" de seguridad.
     *
     * Captura: Exception (clase padre de todas las excepciones)
     *
     * Respuesta ejemplo:
     * {
     *   "timestamp": "2025-05-20T10:30:00",
     *   "status": 500,
     *   "error": "Internal Server Error",
     *   "mensaje": "Ha ocurrido un error inesperado en el servidor",
     *   "path": "/reservas"
     * }
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        // Registrar el error en los logs para depuración
        System.err.println("Error inesperado: " + ex.getMessage());
        ex.printStackTrace();

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ha ocurrido un error inesperado en el servidor",
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}