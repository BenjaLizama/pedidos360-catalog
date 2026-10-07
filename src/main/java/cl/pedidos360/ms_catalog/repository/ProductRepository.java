package cl.pedidos360.ms_catalog.repository;

import cl.pedidos360.ms_catalog.entity.ProductEntity;
import cl.pedidos360.ms_catalog.repository.custom.ProductRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends MongoRepository<ProductEntity, UUID>, ProductRepositoryCustom {
    Optional<ProductEntity> findBySku(String sku);
    boolean existsBySku(String sku);
}
