package com.example.freshflower;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("ci") // 👈 add this line
class FreshflowerApplicationTests {

	@Test
	void contextLoads() {
		// This test just checks if Spring loads correctly.
	}
}
