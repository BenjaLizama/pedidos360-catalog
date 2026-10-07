package cl.pedidos360.ms_catalog.service;

import cl.pedidos360.ms_catalog.dto.response.PriceHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PriceHistoryService {

    Page<PriceHistoryResponse> findByProductId(
            UUID productId,
            Pageable pageable
    );
}
