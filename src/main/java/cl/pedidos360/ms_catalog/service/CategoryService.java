package cl.pedidos360.ms_catalog.service;

import cl.pedidos360.ms_catalog.dto.request.CategoryCreateRequest;
import cl.pedidos360.ms_catalog.dto.request.CategoryStatusUpdateRequest;
import cl.pedidos360.ms_catalog.dto.request.CategoryUpdateRequest;
import cl.pedidos360.ms_catalog.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {

    CategoryResponse create(CategoryCreateRequest request);

    Page<CategoryResponse> findAll(Pageable pageable);

    CategoryResponse findById(UUID id);

    void delete(UUID id);

    CategoryResponse update(
            UUID id,
            CategoryUpdateRequest request
    );

    CategoryResponse updateStatus(
            UUID id,
            CategoryStatusUpdateRequest request
    );
}
