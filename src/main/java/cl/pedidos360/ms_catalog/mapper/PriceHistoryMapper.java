package cl.pedidos360.ms_catalog.mapper;

import cl.pedidos360.ms_catalog.dto.response.PriceHistoryResponse;
import cl.pedidos360.ms_catalog.entity.PriceHistoryEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper responsable de transformar el historial de precios.
 * <p>
 * El historial es generado internamente por el sistema,
 * por lo que no necesitamos un Request DTO para su creación.
 */
@Component
public class PriceHistoryMapper {

    /**
     * Convierte una entidad de historial de precios
     * en su DTO de respuesta.
     */
    public PriceHistoryResponse toResponse(
            PriceHistoryEntity priceHistory
    ) {

        return new PriceHistoryResponse(
                priceHistory.getId(),
                priceHistory.getProductId(),
                priceHistory.getPreviousPrice(),
                priceHistory.getNewPrice(),
                priceHistory.getReason(),
                priceHistory.getCorrelationId(),
                priceHistory.getCreatedAt(),
                priceHistory.getCreatedBy()
        );
    }
}
