package cl.pedidos360.ms_catalog.exception;

import cl.pedidos360.ms_catalog.dto.response.StandardErrorResponse;
import cl.pedidos360.ms_catalog.enums.ErrorCode;
import cl.pedidos360.ms_catalog.mapper.ErrorResponseMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
 * <p>
 * Centraliza el tratamiento de errores producidos por los controllers,
 * servicios y capa de persistencia, transformándolos en respuestas HTTP
 * consistentes mediante StandardErrorResponse.
 * <p>
 * Las excepciones de autenticación y autorización (401/403) son manejadas
 * directamente por Spring Security mediante SecurityAuthenticationEntryPoint
 * y SecurityAccessDeniedHandler.
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseMapper errorResponseMapper;

    /**
     * Maneja errores cuando el recurso solicitado no existe.
     * <p>
     * Ejemplo:
     * intentar consultar un producto o categoría inexistente.
     * <p>
     * HTTP 404 - NOT FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getErrorCode(),
                exception.getMessage(),
                request
        );
    }

    /**
     * Maneja conflictos provocados por recursos que ya existen.
     * <p>
     * Ejemplo:
     * intentar registrar un producto utilizando un SKU existente.
     * <p>
     * HTTP 409 - CONFLICT
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<StandardErrorResponse> handleResourceAlreadyExists(
            ResourceAlreadyExistsException exception,
            HttpServletRequest request
    ) {

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
     * <p>
     * HTTP 409 - CONFLICT
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<StandardErrorResponse> handleInsufficientStock(
            InsufficientStockException exception,
            HttpServletRequest request
    ) {

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
     * <p>
     * Ejemplo:
     * intentar modificar un recurso en un estado que no lo permite.
     * <p>
     * HTTP 409 - CONFLICT
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<StandardErrorResponse> handleInvalidOperation(
            InvalidOperationException exception,
            HttpServletRequest request
    ) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getErrorCode(),
                exception.getMessage(),
                request
        );
    }

    /**
     * Maneja excepciones generales relacionadas con reglas de negocio.
     * <p>
     * HTTP 409 - CONFLICT
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {

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
     * <p>Ocurre cuando una entidad fue modificada por otro proceso
     * después de haber sido consultada y antes de intentar guardar
     * los cambios.</p>
     *
     * @param exception excepción de concurrencia optimista
     * @param request solicitud HTTP actual
     * @return respuesta HTTP 409 Conflict
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<StandardErrorResponse> handleOptimisticLockingFailure(
            OptimisticLockingFailureException exception,
            HttpServletRequest request
    ) {
        StandardErrorResponse response =
                errorResponseMapper.toResponse(
                        HttpStatus.CONFLICT,
                        ErrorCode.INVALID_OPERATION,
                        "El recurso fue modificado por otro proceso. "
                                + "Vuelve a consultar el recurso e inténtalo nuevamente.",
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    /**
     * Maneja errores producidos por las validaciones de Bean Validation
     * utilizadas mediante @Valid.
     * <p>
     * Construye un mapa con los errores asociados a cada campo del request.
     * <p>
     * Ejemplo:
     * {
     *     "name": "El nombre es obligatorio.",
     *     "price": "El precio debe ser mayor a cero."
     * }
     * <p>
     * HTTP 400 - BAD REQUEST
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
     * Maneja errores producidos cuando un parámetro de la petición
     * no puede convertirse al tipo esperado por el controller.
     * <p>
     * Ejemplo:
     * enviar "abc" como ID cuando el endpoint espera un UUID.
     * <p>
     * HTTP 400 - BAD REQUEST
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

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR,
                message,
                request
        );
    }

    /**
     * Maneja errores de integridad provenientes de MongoDB.
     * <p>
     * Principalmente permite transformar errores producidos por índices
     * únicos en una respuesta HTTP controlada.
     * <p>
     * Ejemplo:
     * intentar insertar un SKU que ya existe.
     * <p>
     * HTTP 409 - CONFLICT
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<StandardErrorResponse> handleDuplicateKey(
            DuplicateKeyException exception,
            HttpServletRequest request
    ) {

        return buildResponse(
                HttpStatus.CONFLICT,
                ErrorCode.RESOURCE_ALREADY_EXISTS,
                "El recurso que intenta crear o modificar ya existe.",
                request
        );
    }

    /**
     * Fallback para excepciones que no fueron manejadas explícitamente.
     * <p>
     * Evita exponer detalles internos de Spring, MongoDB u otras
     * dependencias al cliente.
     * <p>
     * Los detalles técnicos pueden ser registrados posteriormente
     * mediante el sistema de logging y observabilidad.
     * <p>
     * HTTP 500 - INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {

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
     * <p>
     * Delega la transformación al ErrorResponseMapper para evitar
     * duplicar la construcción de StandardErrorResponse en cada handler.
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
