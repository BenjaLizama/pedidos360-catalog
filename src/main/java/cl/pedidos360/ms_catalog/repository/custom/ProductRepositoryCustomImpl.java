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

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<ProductEntity> search(String name, UUID categoryId, ProductStatus status, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            criteriaList.add(
                    Criteria.where("name")
                            .regex(name.trim(), "i")
            );
        }

        if (categoryId != null) {
            criteriaList.add(
                    Criteria.where("categoryId")
                            .is(categoryId)
            );
        }

        if (status != null) {
            criteriaList.add(
                    Criteria.where("status")
                            .is(status)
            );
        }

        Query query = new Query();

        if (!criteriaList.isEmpty()) {
            query.addCriteria(
                    new Criteria().andOperator(
                            criteriaList.toArray(new Criteria[0])
                    )
            );
        }

        long total = mongoTemplate.count(
                query,
                ProductEntity.class
        );

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
