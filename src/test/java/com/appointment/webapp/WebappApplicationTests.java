package com.appointment.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.tenancy.enabled=false")
class WebappApplicationTests {

	@Test
	void contextLoads() {
	}

}
