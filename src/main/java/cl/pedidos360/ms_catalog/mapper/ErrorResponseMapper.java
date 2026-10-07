package cl.pedidos360.ms_catalog.mapper;

import cl.pedidos360.ms_catalog.dto.response.StandardErrorResponse;
import cl.pedidos360.ms_catalog.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ErrorResponseMapper {

    public StandardErrorResponse toResponse(
            HttpStatus status,
            ErrorCode errorCode,
            String message,
            HttpServletRequest request
    ) {
        return StandardErrorResponse.builder()
                .status(status.value())
                .code(errorCode.name())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public StandardErrorResponse toValidationResponse(
            Map<String, String> validationErrors,
            HttpServletRequest request
    ) {
        return StandardErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(ErrorCode.VALIDATION_ERROR.name())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("La solicitud contiene datos inválidos.")
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .validationError(validationErrors)
                .build();
    }

    public StandardErrorResponse toInternalServerError(
            Exception exception,
            HttpServletRequest request
    ) {
        return StandardErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code(ErrorCode.INTERNAL_SERVER_ERROR.name())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Ha ocurrido un error interno en el servidor.")
                .developerMessage(exception.getMessage())
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
