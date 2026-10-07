package cl.pedidos360.ms_catalog.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PriceHistoryResponse(
        UUID id,
        UUID productId,
        BigDecimal previousPrice,
        BigDecimal newPrice,
        String reason,
        UUID correlationId,
        Instant createdAt,
        UUID createdBy
) {
}
