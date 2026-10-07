package cl.pedidos360.ms_catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ProductUpdateRequest(

        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres.")
        String name,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres.")
        String description,

        @NotNull(message = "La categoría es obligatoria.")
        UUID categoryId
) {
}
