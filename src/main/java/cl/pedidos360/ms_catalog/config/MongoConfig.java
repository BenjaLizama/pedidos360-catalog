package cl.pedidos360.ms_catalog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Configuración principal de auditoría para MongoDB.
 *
 * <p>Habilita las capacidades de auditoría de Spring Data MongoDB,
 * permitiendo gestionar automáticamente campos como:</p>
 *
 * <ul>
 *     <li>{@code createdAt}</li>
 *     <li>{@code createdBy}</li>
 *     <li>{@code updatedAt}</li>
 *     <li>{@code updatedBy}</li>
 * </ul>
 *
 * <p>La implementación de {@code AuditorAware} utilizada para resolver
 * el usuario autenticado se encuentra en {@code AuditorAwareImpl}.</p>
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {

}
