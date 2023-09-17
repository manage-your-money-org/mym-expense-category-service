package com.rkumar0206.mymexpensecategoryservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {MymExpenseCategoryServiceApplication.class}, loader = SpringBootContextLoader.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MymExpenseCategoryServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
