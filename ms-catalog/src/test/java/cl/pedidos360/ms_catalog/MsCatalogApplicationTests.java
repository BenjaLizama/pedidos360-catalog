package cl.pedidos360.ms_catalog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MsCatalogApplicationTests {

	@Test
	void contextLoads() {
	}

}
