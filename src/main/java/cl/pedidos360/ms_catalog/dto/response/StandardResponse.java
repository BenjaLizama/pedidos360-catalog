package cl.pedidos360.ms_catalog.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardResponse<T>(
        Integer status,
        String message,
        T data
) {
    public static <T> StandardResponse<T> created(String message, T data) {
        return new StandardResponse<>(HttpStatus.CREATED.value(), message, data);
    }

    public static <T> StandardResponse<T> ok(String message, T data) {
        return new StandardResponse<>(HttpStatus.OK.value(), message, data);
    }

    public static <T> StandardResponse<T> noContent(String message) {
        return new StandardResponse<>(
                HttpStatus.NO_CONTENT.value(),
                message,
                null
        );
    }
}
