package cl.pedidos360.ms_catalog.repository;

import cl.pedidos360.ms_catalog.entity.CategoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends MongoRepository<CategoryEntity, UUID> {
    Optional<CategoryEntity> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
