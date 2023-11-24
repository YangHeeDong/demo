package com.example.demo.Question;

import com.example.demo.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public Page<Question> getList(int page){
        List<Sort.Order> sorts = new ArrayList<>();

        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page,10,Sort.by(sorts));

        return this.questionRepository.findAll(pageable);
    }

}
