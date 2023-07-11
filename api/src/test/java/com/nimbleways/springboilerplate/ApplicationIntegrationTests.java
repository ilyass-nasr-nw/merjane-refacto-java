package com.nimbleways.springboilerplate;

import com.nimbleways.springboilerplate.utils.Annotations.SetupDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SetupDatabase
@SpringBootTest
class ApplicationIntegrationTests {

	@Test
	void contextLoads() {
	}

}
