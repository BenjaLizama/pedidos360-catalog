package cl.pedidos360.ms_catalog.security;

import cl.pedidos360.ms_catalog.dto.response.StandardErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        StandardErrorResponse errorResponse = StandardErrorResponse.builder()
                .status(HttpServletResponse.SC_FORBIDDEN)
                .code("ACCESS_DENIED")
                .error("FORBIDDEN")
                .message("No tiene permiso para acceder a este recurso.")
                .developerMessage(accessDeniedException.getMessage())
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(
                response.getOutputStream(),
                errorResponse
        );
    }
}
