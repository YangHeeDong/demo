package com.example.demo.Question;

import com.example.demo.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public void create(String subject, String content){
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());

        this.questionRepository.save(question);
    }

    public List<Question> getList(){
        return questionRepository.findAll();
    }
    public Question getQusetion(Integer id){

        Optional<Question> question = questionRepository.findById(id);

        if(question.isPresent()){
            return question.get();
        }
        else{
            throw new DataNotFoundException("question not found");
        }
    }


}
