package cl.pedidos360.ms_catalog.mapper;

import cl.pedidos360.ms_catalog.dto.response.StockMovementResponse;
import cl.pedidos360.ms_catalog.entity.StockMovementEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper responsable de transformar los movimientos de stock.
 * <p>
 * Los movimientos son registros generados por las operaciones
 * del sistema y no son creados directamente por el cliente.
 */
@Component
public class StockMovementMapper {

    /**
     * Convierte una entidad de movimiento de stock
     * en su DTO de respuesta.
     */
    public StockMovementResponse toResponse(
            StockMovementEntity stockMovement
    ) {

        return new StockMovementResponse(
                stockMovement.getId(),
                stockMovement.getProductId(),
                stockMovement.getType(),
                stockMovement.getQuantity(),
                stockMovement.getPreviousStock(),
                stockMovement.getResultingStock(),
                stockMovement.getReason(),
                stockMovement.getCorrelationId(),
                stockMovement.getCreatedAt(),
                stockMovement.getCreatedBy()
        );
    }
}
