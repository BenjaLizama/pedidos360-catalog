package cl.pedidos360.ms_catalog.config;

import cl.pedidos360.ms_catalog.audit.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public AuditorAware<String> auditorAware(AuditorAwareImpl auditorAwareImpl) {
        return auditorAwareImpl;
    }
}
