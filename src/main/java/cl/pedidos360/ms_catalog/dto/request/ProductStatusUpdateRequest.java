package cl.pedidos360.ms_catalog.dto.request;

import cl.pedidos360.ms_catalog.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record ProductStatusUpdateRequest(

        @NotNull(message = "El estado es obligatorio.")
        ProductStatus status
) {
}
