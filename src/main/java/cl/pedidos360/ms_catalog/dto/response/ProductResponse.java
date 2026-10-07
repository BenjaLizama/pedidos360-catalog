package cl.pedidos360.ms_catalog.dto.response;

import cl.pedidos360.ms_catalog.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        UUID categoryId,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
