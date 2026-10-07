package cl.pedidos360.ms_catalog.dto.response;

import cl.pedidos360.ms_catalog.enums.StockMovementType;

import java.time.Instant;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        UUID productId,
        StockMovementType type,
        Integer quantity,
        Integer previousStock,
        Integer resultingStock,
        String reason,
        String correlationId,
        Instant createdAt,
        String createdBy
) {
}
