package cl.pedidos360.ms_catalog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductStockUpdateRequest(

        @NotNull(message = "La cantidad es obligatoria.")
        @Positive(message = "La cantidad debe ser mayor a cero.")
        Integer quantity,

        @NotNull(message = "El motivo es obligatorio.")
        String reason
) {
}
