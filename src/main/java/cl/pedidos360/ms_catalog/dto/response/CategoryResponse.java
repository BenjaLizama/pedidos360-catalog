package cl.pedidos360.ms_catalog.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String description,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
