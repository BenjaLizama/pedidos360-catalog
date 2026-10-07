package cl.pedidos360.ms_catalog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Datos necesarios para realizar un ajuste manual de inventario.
 *
 * <p>La operación utiliza una cantidad positiva y registra un movimiento
 * de tipo {@code ADJUSTMENT}. La lógica de negocio determina el efecto
 * final sobre el stock.</p>
 *
 * @param quantity cantidad de unidades involucradas en el ajuste
 * @param reason motivo del ajuste manual de inventario
 */
public record ProductStockUpdateRequest(

        @NotNull(message = "La cantidad es obligatoria.")
        @Positive(message = "La cantidad debe ser mayor a cero.")
        Integer quantity,

        @NotNull(message = "El motivo es obligatorio.")
        String reason
) {
}
