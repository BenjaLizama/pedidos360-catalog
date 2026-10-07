package cl.pedidos360.ms_catalog.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductPriceUpdateRequest(

        @NotNull(message = "El precio es obligatorio.")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a cero.")
        BigDecimal price,

        String reason
) {
}
