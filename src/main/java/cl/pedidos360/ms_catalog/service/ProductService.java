package cl.pedidos360.ms_catalog.service;

import cl.pedidos360.ms_catalog.dto.request.*;
import cl.pedidos360.ms_catalog.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Servicio principal encargado de gestionar la lógica de negocio
 * relacionada con los productos del catálogo.
 *
 * <p>Centraliza las operaciones de creación, consulta, actualización
 * y eliminación lógica de productos, además de las operaciones
 * específicas relacionadas con el inventario.</p>
 *
 * <p>Las modificaciones de precio y las operaciones derivadas de ventas
 * pueden ser gestionadas mediante flujos específicos, manteniendo
 * separadas las responsabilidades de cada operación.</p>
 */
public interface ProductService {

    /**
     * Crea un nuevo producto en el catálogo.
     *
     * @param request datos necesarios para crear el producto
     * @return información del producto creado
     */
    ProductResponse create(ProductCreateRequest request);

    /**
     * Elimina lógicamente un producto.
     *
     * <p>El producto permanece almacenado para conservar su trazabilidad
     * e historial.</p>
     *
     * @param id identificador único del producto
     */
    void delete(UUID id);

    /**
     * Busca productos aplicando filtros opcionales y paginación.
     *
     * @param name nombre o texto utilizado para filtrar productos
     * @param categoryId identificador de la categoría utilizada como filtro
     * @param status estado del producto utilizado como filtro
     * @param pageable configuración de paginación y ordenamiento
     * @return página de productos que cumplen los filtros
     */
    Page<ProductResponse> search(
            String name,
            UUID categoryId,
            String status,
            Pageable pageable
    );

    /**
     * Actualiza la información descriptiva de un producto.
     *
     * @param id identificador único del producto
     * @param request nuevos datos descriptivos del producto
     * @return información actualizada del producto
     */
    ProductResponse update(
            UUID id,
            ProductUpdateRequest request
    );

    /**
     * Actualiza exclusivamente el estado de un producto.
     *
     * @param id identificador único del producto
     * @param request nuevo estado del producto
     * @return información actualizada del producto
     */
    ProductResponse updateStatus(
            UUID id,
            ProductStatusUpdateRequest request
    );

    /**
     * Realiza un ajuste manual sobre el inventario de un producto.
     *
     * @param id identificador único del producto
     * @param request datos del ajuste de inventario
     * @return información actualizada del producto
     */
    ProductResponse updateStock(
            UUID id,
            ProductStockUpdateRequest request
    );

    /**
     * Incrementa el stock disponible mediante una operación
     * de reposición.
     *
     * @param id identificador único del producto
     * @param request datos de la reposición
     * @return información actualizada del producto
     */
    ProductResponse restock(
            UUID id,
            ProductRestockRequest request
    );
}
