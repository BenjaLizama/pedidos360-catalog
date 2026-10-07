package cl.pedidos360.ms_catalog.dto.request;

import jakarta.validation.constraints.NotNull;

public record CategoryStatusUpdateRequest(

        @NotNull(message = "El estado es obligatorio.")
        Boolean active
) {
}
