package cl.pedidos360.ms_catalog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Datos necesarios para realizar una reposición de inventario.
 *
 * <p>La operación incrementa el stock disponible y genera un movimiento
 * de inventario de tipo {@code RESTOCK}.</p>
 *
 * @param quantity cantidad de unidades que serán agregadas al stock
 * @param reason motivo de la reposición de inventario
 */
public record ProductRestockRequest(

        @NotNull(message = "La cantidad es obligatoria.")
        @Positive(message = "La cantidad debe ser mayor a cero.")
        Integer quantity,

        @NotNull(message = "El motivo es obligatorio.")
        String reason
) {
}
