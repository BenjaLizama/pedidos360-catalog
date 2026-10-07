package cl.pedidos360.ms_catalog.entity;

import cl.pedidos360.ms_catalog.audit.AuditableEntity;
import cl.pedidos360.ms_catalog.enums.StockMovementType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "stock_movements")
@CompoundIndex(
        name = "product_created_at_idx",
        def = "{'productId': 1, 'createdAt': -1}"
)
public class StockMovementEntity extends AuditableEntity {

    @Id
    private UUID id;

    private UUID productId;

    private StockMovementType type;

    private Integer quantity;

    private Integer previousStock;

    private Integer resultingStock;

    private String reason;

    @Indexed
    private UUID correlationId;
}
