package cl.pedidos360.ms_catalog.entity;

import cl.pedidos360.ms_catalog.audit.AuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "categories")
public class CategoryEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private String name;

    private String description;

    @Indexed
    private Boolean active;

    @Version
    private Long version;
}
