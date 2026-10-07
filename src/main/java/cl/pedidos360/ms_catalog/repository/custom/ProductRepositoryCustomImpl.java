package cl.pedidos360.ms_catalog.repository.custom;

import cl.pedidos360.ms_catalog.entity.ProductEntity;
import cl.pedidos360.ms_catalog.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementación de las consultas dinámicas de productos.
 *
 * <p>Utiliza {@link MongoTemplate} para construir dinámicamente la
 * consulta en función de los filtros proporcionados.</p>
 *
 * <p>La implementación permite combinar nombre, categoría y estado
 * sin necesidad de crear múltiples métodos de consulta para cada
 * combinación posible de filtros.</p>
 */
@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    /**
     * Plantilla utilizada para construir y ejecutar consultas
     * directamente sobre MongoDB.
     */
    private final MongoTemplate mongoTemplate;

    /**
     * Ejecuta una búsqueda dinámica de productos.
     *
     * <p>Cada filtro es agregado únicamente cuando posee un valor válido.
     * Cuando se proporcionan varios filtros, estos se combinan mediante
     * una condición lógica AND.</p>
     *
     * <p>La búsqueda por nombre no distingue entre mayúsculas y minúsculas.
     * Una vez construida la consulta, se obtiene el total de registros
     * coincidentes y posteriormente se aplica la paginación.</p>
     *
     * @param name texto utilizado para buscar productos por nombre
     * @param categoryId identificador de la categoría utilizada como filtro
     * @param status estado utilizado como filtro
     * @param pageable configuración de paginación y ordenamiento
     * @return página de productos que cumplen los filtros proporcionados
     */
    @Override
    public Page<ProductEntity> search(String name, UUID categoryId, ProductStatus status, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();

        /*
         * Agrega el filtro por nombre cuando se proporciona un valor.
         *
         * La búsqueda utiliza una expresión regular sin distinción
         * entre mayúsculas y minúsculas.
         */
        if (name != null && !name.isBlank()) {
            criteriaList.add(
                    Criteria.where("name")
                            .regex(name.trim(), "i")
            );
        }

        /*
         * Agrega el filtro por categoría cuando se proporciona
         * un identificador.
         */
        if (categoryId != null) {
            criteriaList.add(
                    Criteria.where("categoryId")
                            .is(categoryId)
            );
        }

        /*
         * Agrega el filtro por estado cuando se proporciona.
         */
        if (status != null) {
            criteriaList.add(
                    Criteria.where("status")
                            .is(status)
            );
        }

        Query query = new Query();

        /*
         * Cuando existen filtros, se combinan mediante AND para que
         * el producto deba cumplir todas las condiciones especificadas.
         */
        if (!criteriaList.isEmpty()) {
            query.addCriteria(
                    new Criteria().andOperator(
                            criteriaList.toArray(new Criteria[0])
                    )
            );
        }

        /*
         * Obtiene la cantidad total de documentos que cumplen los filtros.
         * Este valor es necesario para construir correctamente la respuesta
         * paginada.
         */
        long total = mongoTemplate.count(
                query,
                ProductEntity.class
        );

        /*
         * Aplica la configuración de paginación y ordenamiento antes
         * de ejecutar la consulta definitiva.
         */
        query.with(pageable);

        List<ProductEntity> products = mongoTemplate.find(
                query,
                ProductEntity.class
        );

        return new PageImpl<>(
                products,
                pageable,
                total
        );
    }
}
