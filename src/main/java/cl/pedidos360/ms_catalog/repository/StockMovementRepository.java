package cl.pedidos360.ms_catalog.repository;

import cl.pedidos360.ms_catalog.entity.StockMovementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface StockMovementRepository extends MongoRepository<StockMovementEntity, UUID> {
    Page<StockMovementEntity> findByProductId(UUID productId, Pageable pageable);
}
