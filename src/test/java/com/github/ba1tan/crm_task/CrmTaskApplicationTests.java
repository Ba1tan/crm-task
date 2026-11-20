package com.github.ba1tan.crm_task;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class CrmTaskApplicationTests {

	@Test
	void contextLoads() {
	}

}
