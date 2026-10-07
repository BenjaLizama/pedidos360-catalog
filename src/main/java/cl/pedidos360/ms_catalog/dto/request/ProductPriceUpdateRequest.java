package cl.pedidos360.ms_catalog.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Datos necesarios para actualizar el precio de un producto.
 *
 * <p>El cambio de precio se maneja mediante una operación específica
 * para permitir registrar el valor anterior, el nuevo valor y el
 * motivo del cambio en el historial de precios.</p>
 *
 * @param price nuevo precio del producto
 * @param reason motivo asociado al cambio de precio
 */
public record ProductPriceUpdateRequest(

        @NotNull(message = "El precio es obligatorio.")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a cero.")
        BigDecimal price,

        String reason
) {
}
