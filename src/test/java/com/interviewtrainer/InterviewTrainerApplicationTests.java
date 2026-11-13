package com.interviewtrainer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// In-memory DB so tests never touch (or lock) the local ./data database.
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:context-test;DB_CLOSE_DELAY=-1")
class InterviewTrainerApplicationTests {

	@Test
	void contextLoads() {
	}

}
