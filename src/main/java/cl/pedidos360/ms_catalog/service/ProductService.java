package cl.pedidos360.ms_catalog.service;

import cl.pedidos360.ms_catalog.dto.request.*;
import cl.pedidos360.ms_catalog.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    ProductResponse create(ProductCreateRequest request);

    void delete(UUID id);

    Page<ProductResponse> search(
            String name,
            UUID categoryId,
            String status,
            Pageable pageable
    );

    ProductResponse update(
            UUID id,
            ProductUpdateRequest request
    );

    ProductResponse updateStatus(
            UUID id,
            ProductStatusUpdateRequest request
    );

    ProductResponse updateStock(
            UUID id,
            ProductStockUpdateRequest request
    );

    ProductResponse restock(
            UUID id,
            ProductRestockRequest request
    );
}
