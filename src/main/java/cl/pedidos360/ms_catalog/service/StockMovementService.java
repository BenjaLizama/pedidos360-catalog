package cl.pedidos360.ms_catalog.service;

import cl.pedidos360.ms_catalog.dto.response.StockMovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StockMovementService {

    Page<StockMovementResponse> findByProductId(
            UUID productId,
            Pageable pageable
    );
}
