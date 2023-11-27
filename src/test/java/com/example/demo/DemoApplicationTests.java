package com.example.demo;

import com.example.demo.Question.QuestionRepository;
import com.example.demo.Question.QuestionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private QuestionService questionService;

	//Transactional의 역할 : 아래 메서드가 끝날때까지 DB 세션을 유지시켜줌
	//@Transactional
	@Test
	void testJpa() {

	}

}
