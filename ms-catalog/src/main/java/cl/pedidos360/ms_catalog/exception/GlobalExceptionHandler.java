package cl.pedidos360.ms_catalog.exception;

import cl.pedidos360.ms_catalog.dto.response.StandardErrorResponse;
import cl.pedidos360.ms_catalog.enums.ErrorCode;
import cl.pedidos360.ms_catalog.mapper.ErrorResponseMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones de la aplicación.
 *
 * <p>Centraliza el tratamiento de errores producidos por los controllers,
 * servicios y capa de persistencia, transformándolos en respuestas HTTP
 * consistentes mediante {@link StandardErrorResponse}.</p>
 *
 * <p>Las excepciones de autenticación y autorización (401/403) son
 * manejadas directamente por Spring Security mediante
 * {@code SecurityAuthenticationEntryPoint} y
 * {@code SecurityAccessDeniedHandler}.</p>
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseMapper errorResponseMapper;

    /**
     * Maneja errores cuando el recurso solicitado no existe.
     *
     * <p>HTTP 404 - NOT FOUND</p>
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "Recurso no encontrado | path={} | method={} | message={}",
                request.getRequestURI(),
                request.getMethod(),
                exception.getMessage()
        );

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getErrorCode(),
                exception.getMessage(),
                request
        );
    }

    /**
     * Maneja conflictos provocados por recursos que ya existen.
     *
     * <p>HTTP 409 - CONFLICT</p>
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<StandardErrorResponse> handleResourceAlreadyExists(
            ResourceAlreadyExistsException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "Recurso ya existente | path={} | method={} | message={}",
                request.getRequestURI(),
                request.getMethod(),
                exception.getMessage()
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getErrorCode(),
                exception.getMessage(),
                request
        );
    }

    /**
     * Maneja operaciones que intentan utilizar una cantidad de stock
     * superior a la disponible.
     *
     * <p>HTTP 409 - CONFLICT</p>
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<StandardErrorResponse> handleInsufficientStock(
            InsufficientStockException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "Stock insuficiente | path={} | method={} | message={}",
                request.getRequestURI(),
                request.getMethod(),
                exception.getMessage()
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getErrorCode(),
                exception.getMessage(),
                request
        );
    }

    /**
     * Maneja operaciones que no están permitidas según las reglas
     * de negocio del catálogo.
     *
     * <p>HTTP 409 - CONFLICT</p>
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<StandardErrorResponse> handleInvalidOperation(
            InvalidOperationException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "Operación inválida | path={} | method={} | message={}",
                request.getRequestURI(),
                request.getMethod(),
                exception.getMessage()
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getErrorCode(),
                exception.getMessage(),
                request
        );
    }

    /**
     * Maneja excepciones generales relacionadas con reglas de negocio.
     *
     * <p>HTTP 409 - CONFLICT</p>
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "Error de negocio | path={} | method={} | code={} | message={}",
                request.getRequestURI(),
                request.getMethod(),
                exception.getErrorCode(),
                exception.getMessage()
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getErrorCode(),
                exception.getMessage(),
                request
        );
    }

    /**
     * Maneja conflictos producidos por concurrencia optimista.
     *
     * <p>Ocurre cuando otro proceso modifica el mismo documento
     * después de que la solicitud actual lo haya leído.</p>
     *
     * <p>HTTP 409 - CONFLICT</p>
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<StandardErrorResponse> handleOptimisticLockingFailure(
            OptimisticLockingFailureException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "Conflicto de concurrencia optimista | path={} | method={} | message={}",
                request.getRequestURI(),
                request.getMethod(),
                exception.getMessage()
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                ErrorCode.OPTIMISTIC_LOCK_CONFLICT,
                "El recurso fue modificado por otro proceso. "
                        + "Vuelve a consultar el recurso e inténtalo nuevamente.",
                request
        );
    }

    /**
     * Maneja errores producidos por las validaciones de Bean Validation.
     *
     * <p>HTTP 400 - BAD REQUEST</p>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        Map<String, String> validationErrors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        log.warn(
                "Error de validación | path={} | method={} | errors={}",
                request.getRequestURI(),
                request.getMethod(),
                validationErrors
        );

        StandardErrorResponse response =
                errorResponseMapper.toValidationResponse(
                        validationErrors,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Maneja errores producidos cuando un parámetro no puede convertirse
     * al tipo esperado por el controller.
     *
     * <p>HTTP 400 - BAD REQUEST</p>
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {

        String message = String.format(
                "El parámetro '%s' tiene un formato inválido.",
                exception.getName()
        );

        log.warn(
                "Parámetro inválido | path={} | method={} | parameter={} | value={}",
                request.getRequestURI(),
                request.getMethod(),
                exception.getName(),
                exception.getValue()
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR,
                message,
                request
        );
    }

    /**
     * Maneja errores de integridad provenientes de MongoDB.
     *
     * <p>Principalmente permite transformar errores producidos por
     * índices únicos en una respuesta HTTP controlada.</p>
     *
     * <p>HTTP 409 - CONFLICT</p>
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<StandardErrorResponse> handleDuplicateKey(
            DuplicateKeyException exception,
            HttpServletRequest request
    ) {

        log.warn(
                "Conflicto de clave duplicada | path={} | method={} | message={}",
                request.getRequestURI(),
                request.getMethod(),
                exception.getMessage()
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                ErrorCode.RESOURCE_ALREADY_EXISTS,
                "El recurso que intenta crear o modificar ya existe.",
                request
        );
    }

    /**
     * Fallback para excepciones que no fueron manejadas explícitamente.
     *
     * <p>Evita exponer detalles internos de Spring, MongoDB u otras
     * dependencias al cliente.</p>
     *
     * <p>Los detalles técnicos completos se registran mediante logging
     * para facilitar el diagnóstico.</p>
     *
     * <p>HTTP 500 - INTERNAL SERVER ERROR</p>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {

        log.error(
                "Error inesperado | path={} | method={} | message={}",
                request.getRequestURI(),
                request.getMethod(),
                exception.getMessage(),
                exception
        );

        StandardErrorResponse response =
                errorResponseMapper.toInternalServerError(
                        exception,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /**
     * Construye una respuesta estándar para las excepciones controladas
     * de la aplicación.
     */
    private ResponseEntity<StandardErrorResponse> buildResponse(
            HttpStatus status,
            ErrorCode errorCode,
            String message,
            HttpServletRequest request
    ) {

        StandardErrorResponse response =
                errorResponseMapper.toResponse(
                        status,
                        errorCode,
                        message,
                        request
                );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
