package cl.pedidos360.ms_catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(

        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
        String name,

        @Size(max = 300, message = "La descripción no puede superar los 300 caracteres.")
        String description
) {
}
