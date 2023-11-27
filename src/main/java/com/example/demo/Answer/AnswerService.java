package com.example.demo.Answer;

import com.example.demo.Question.Question;
import com.example.demo.User.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    public void create(Question question, String content, SiteUser author){
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setAuthor(author);
        answer.setQuestion(question);

        answerRepository.save(answer);
    }
}
