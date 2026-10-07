package cl.pedidos360.ms_catalog.entity;

import cl.pedidos360.ms_catalog.audit.AuditableEntity;
import cl.pedidos360.ms_catalog.enums.ProductStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "products")
public class ProductEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private String sku;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private UUID categoryId;

    @Indexed
    private ProductStatus status;

    @Version
    private Long version;
}
