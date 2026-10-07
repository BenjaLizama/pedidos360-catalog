package cl.pedidos360.ms_catalog.entity;

import cl.pedidos360.ms_catalog.audit.AuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "price_history")
@CompoundIndex(
        name = "product_created_at_idx",
        def = "{'productId': 1, 'createdAt': -1}"
)
public class PriceHistoryEntity extends AuditableEntity {

    @Id
    private String id;

    private String productId;

    private BigDecimal previousPrice;

    private BigDecimal newPrice;

    private String reason;

    private String correlationId;
}
