package com.example.demo;

import com.example.demo.Question.QuestionRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoApplicationTests {

	private QuestionRepository questionRepository;

	//아래 메서드가 끝날때까지 DB 세션을 유지시켜줌
	@Transactional
	@Test
	void testJpa() {

	}

}
