package com.interviewtrainer;

import com.interviewtrainer.llm.FakeLlmClient;
import com.interviewtrainer.llm.LlmClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

// Uses the in-memory test DB from src/test/resources/config/application.properties.
@SpringBootTest
class InterviewTrainerApplicationTests {

	@Test
	void contextLoadsWithTheFakeLlmClient(@Autowired LlmClient llmClient) {
		assertThat(llmClient).isInstanceOf(FakeLlmClient.class);
	}

}
