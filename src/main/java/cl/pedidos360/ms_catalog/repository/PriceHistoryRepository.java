package cl.pedidos360.ms_catalog.repository;

import cl.pedidos360.ms_catalog.entity.PriceHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface PriceHistoryRepository extends MongoRepository<PriceHistoryEntity, UUID> {
    Page<PriceHistoryEntity> findByProductId(UUID productId, Pageable pageable);
}
