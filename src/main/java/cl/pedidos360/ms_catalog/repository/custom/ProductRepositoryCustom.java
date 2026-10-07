package cl.pedidos360.ms_catalog.repository.custom;

import cl.pedidos360.ms_catalog.entity.ProductEntity;
import cl.pedidos360.ms_catalog.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductRepositoryCustom {
    Page<ProductEntity> search(
            String name,
            UUID categoryId,
            ProductStatus status,
            Pageable pageable
    );
}
